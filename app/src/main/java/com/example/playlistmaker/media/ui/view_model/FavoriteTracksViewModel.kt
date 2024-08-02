package com.example.playlistmaker.media.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.db.FavoritesInteractor
import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.launch

class FavoriteTracksViewModel(
    private val favoritesInteractor: FavoritesInteractor
) : ViewModel() {

    init {
        viewModelScope.launch {
            favoritesInteractor.favoritesTracks().collect { tracks ->
                processResult(tracks)
            }
        }
    }

    private val _state = MutableLiveData<FavoritesState>()
    val state: LiveData<FavoritesState> get() = _state

    private fun processResult(tracks: List<Track>) {
        if (tracks.isEmpty()) {
            renderState(FavoritesState.Empty("Нет избранных треков"))
        } else {
            renderState(FavoritesState.Content(tracks))
        }
    }

    fun renderState(state: FavoritesState) {
        _state.postValue(state)
    }
}