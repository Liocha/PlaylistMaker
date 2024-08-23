package com.example.playlistmaker.media.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.interactor.PlaylistInteractor
import com.example.playlistmaker.media.domain.model.Playlist
import kotlinx.coroutines.launch

class UpdatePlaylistViewModel(playlistInteractor: PlaylistInteractor) : CreatePlaylistViewModel(
    playlistInteractor
) {
    private val _playlistData = MutableLiveData<Playlist>()
    val playlistData: LiveData<Playlist> get() = _playlistData

    private val _navigateBack = MutableLiveData<Unit>()
    val navigateBack: LiveData<Unit> get() = _navigateBack

    fun loadPlaylist(it: Int) {
        viewModelScope.launch {
            val playlist = playlistInteractor.getPlaylistByid(it)
            _playlistData.value = playlist

            _name.value = playlist.name
            _description.value = playlist.description
        }
    }

    fun onSaveHandler() {
        val loadedPlaylist = _playlistData.value ?: return

        viewModelScope.launch {

            val updatedName = if (loadedPlaylist.name != _name.value) {
                _name.value
            } else {
                null
            }

            val updatedDescription = if (loadedPlaylist.description != _description.value) {
                _description.value
            } else {
                null
            }

            val updatedImagePath = _selectedImageUri.value?.let { uri ->
                playlistInteractor.saveImageToPrivateStorage(uri)
            }

            playlistInteractor.updatePlaylistData(
                id = loadedPlaylist.id,
                name = updatedName,
                description = updatedDescription,
                imagePath = updatedImagePath
            )
            _navigateBack.value = Unit
        }
    }
}
