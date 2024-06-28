package com.example.playlistmaker.search.ui.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.domain.use_case.ClearSearchHistory
import com.example.playlistmaker.search.domain.use_case.GetSearchHistory
import com.example.playlistmaker.search.domain.use_case.SaveSearchHistory
import com.example.playlistmaker.search.domain.use_case.SearchTracks
import com.example.playlistmaker.utils.Debounce
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SearchViewModel(
    application: Application,
    private val searchTracksUseCase: SearchTracks,
    private val getSearchHistoryUseCase: GetSearchHistory,
    private val saveSearchHistoryUseCase: SaveSearchHistory,
    private val clearSearchHistoryUseCase: ClearSearchHistory
) : AndroidViewModel(application) {
    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

    private val _state = MutableLiveData<SearchState>()
    val state: LiveData<SearchState> get() = _state

    private val _isSearchHistoryVisible = MutableLiveData<Boolean>()
    val isSearchHistoryVisible: LiveData<Boolean> get() = _isSearchHistoryVisible

    private val _searchHistory = MutableLiveData<List<Track>>()
    val searchHistory: LiveData<List<Track>> get() = _searchHistory

    private val _searchQuery = MutableLiveData<String>().apply { value = "" }
    val searchQuery: LiveData<String> get() = _searchQuery

    private val _showToast = SingleLiveEvent<String>()
    val showToast: LiveData<String> get() = _showToast

    private var latestSearchText: String? = null

    private var serchJob: Job? = null

    private val searchDebounceDelay =
        Debounce<String>(SEARCH_DEBOUNCE_DELAY, viewModelScope, true) { changedText ->
            searchRequest(changedText)
        }

    fun searchDebounce(changedText: String) {

        if (latestSearchText == changedText) {
            return
        }

        this.latestSearchText = changedText

        searchDebounceDelay.debounce(changedText)
    }

    private fun searchRequest(newSearchText: String) {
        if (newSearchText.isEmpty()) {
            return
        }
        renderState(SearchState.Loadind)

        serchJob = viewModelScope.launch {
            searchTracksUseCase.execute(newSearchText).collect { pair ->
                processResult(pair.first, pair.second)
            }
        }
    }

    private fun processResult(foundTracks: List<Track>?, errorMessage: String?) {
        val tracks = mutableListOf<Track>()
        if (foundTracks != null) {
            tracks.addAll(foundTracks)
        }
        when {
            errorMessage != null -> {
                renderState(SearchState.Error(errorMessage))
                _showToast.postValue(errorMessage!!)
            }

            tracks.isEmpty() -> {
                renderState(SearchState.Empty)
            }

            else -> {
                renderState(SearchState.Content(tracks))
            }

        }
    }

    private fun renderState(state: SearchState) {
        _state.postValue(state)
    }

    fun onSearchFocusGained() {
        if (_searchQuery.value == "") {
            loadSearchHistory()
            if ((_searchHistory.value?.size ?: 0) > 0) {
                _isSearchHistoryVisible.value = true
            }
        }
    }

    private fun loadSearchHistory() {
        _searchHistory.value = getSearchHistoryUseCase.execute()
    }

    fun addTrackToSearchHistory(track: Track) {
        saveSearchHistoryUseCase.execute(track)
    }

    fun clearSearchHistory() {
        clearSearchHistoryUseCase.execute()
        loadSearchHistory()
        _isSearchHistoryVisible.value = false
    }

    fun clearSearchInput() {
        _searchQuery.value = ""
        searchDebounceDelay.cancel()
        serchJob?.cancel()
        renderState(SearchState.Content(listOf()))
        setSearchHistoryVisible(true)
    }

    fun onSearchQueryChanged(query: String) {
        if (_searchQuery.value == query) {
            return
        }
        if (query == "") {
            this.latestSearchText = ""
            clearSearchInput()
            return
        }
        _searchQuery.value = query
        searchDebounce(query)
    }

    fun setSearchHistoryVisible(visible: Boolean) {
        if (visible) {
            loadSearchHistory()
            if ((_searchHistory.value?.size ?: 0) > 0) {
                _isSearchHistoryVisible.value = true
            }
        } else {
            _isSearchHistoryVisible.value = false
        }
    }

    fun repeatLastSearchQuery() {
        this.latestSearchText = ""
        _searchQuery.value?.let { searchDebounce(it) }
    }
}