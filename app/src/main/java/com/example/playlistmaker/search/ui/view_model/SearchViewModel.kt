package com.example.playlistmaker.search.ui.view_model

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.playlistmaker.search.domain.consumer.Consumer
import com.example.playlistmaker.search.domain.consumer.ConsumerData
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.domain.use_case.ClearSearchHistory
import com.example.playlistmaker.search.domain.use_case.GetSearchHistory
import com.example.playlistmaker.search.domain.use_case.SaveSearchHistory
import com.example.playlistmaker.search.domain.use_case.SearchTracks

class SearchViewModel(
    application: Application,
    private val searchTracksUseCase: SearchTracks,
    private val getSearchHistoryUseCase: GetSearchHistory,
    private val saveSearchHistoryUseCase: SaveSearchHistory,
    private val clearSearchHistoryUseCase: ClearSearchHistory
) : AndroidViewModel(application) {
    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private val SEARCH_REQUEST_TOKEN = Any()
    }

    private val handler = Handler(Looper.getMainLooper())
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

    fun searchDebounce(changedText: String) {
        if (latestSearchText == changedText) {
            return
        }
        this.latestSearchText = changedText
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
        val searchRunnable = Runnable { searchRequest(changedText) }

        val postTime = SystemClock.uptimeMillis() + SEARCH_DEBOUNCE_DELAY
        handler.postAtTime(
            searchRunnable,
            SEARCH_REQUEST_TOKEN,
            postTime,
        )
    }

    private fun searchRequest(newSearchText: String) {
        if (newSearchText.isEmpty()) {
            return
        }
        renderState(SearchState.Loadind)
        searchTracksUseCase.execute(newSearchText, object : Consumer {
            override fun consume(data: ConsumerData) {
                val tracks = mutableListOf<Track>()
                when (data) {
                    is ConsumerData.Data -> {
                        if (data.data.isEmpty()) {
                            renderState(SearchState.Empty)
                        } else {
                            tracks.addAll(data.data)
                            renderState(SearchState.Content(tracks))
                        }
                    }

                    is ConsumerData.Error -> {
                        renderState(SearchState.Error(data.message))
                        _showToast.postValue(data.message)
                    }

                    is ConsumerData.NetworkError -> {
                        renderState(SearchState.Error(data.message))
                        _showToast.postValue(data.message)
                    }
                }
            }
        })
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
        renderState(SearchState.Content(listOf()))
        setSearchHistoryVisible(true)
    }

    fun onSearchQueryChanged(query: String) {
        if (_searchQuery.value == query) {
            return
        }
        if (query == "") {
            this.latestSearchText = ""
            handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
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