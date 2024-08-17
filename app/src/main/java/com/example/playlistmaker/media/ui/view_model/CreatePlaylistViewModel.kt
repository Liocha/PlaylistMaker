package com.example.playlistmaker.media.ui.view_model

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.interactor.PlaylistInteractor
import com.example.playlistmaker.media.domain.model.Playlist
import kotlinx.coroutines.launch

class CreatePlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private val _name = MutableLiveData("")
    private val _description = MutableLiveData("")

    private val _canCreatePlaylist = MutableLiveData(false)
    val canCreatePlaylist: LiveData<Boolean> get() = _canCreatePlaylist

    private val _navigationState = MutableLiveData<NavigationState>(NavigationState.DefaultNothing)
    val navigationState: LiveData<NavigationState> get() = _navigationState

    private val _selectedImageUri = MutableLiveData<Uri>()

    fun onNameChanged(name: String) {
        _name.postValue(name)
        _canCreatePlaylist.postValue(name.isNotEmpty())
    }

    fun onDescriptionChanged(description: String) {
        _description.value = description

    }

    fun onCreateHandler() {
        if (_name.value.isNullOrEmpty()) {
            return
        }

        viewModelScope.launch {
           val savedImagePath = _selectedImageUri.value?.let { uri ->
                playlistInteractor.saveImageToPrivateStorage(uri)
            } ?: ""

            val playlist = Playlist(
                name = _name.value.toString(),
                description = _description.value.toString(),
                pathCover = savedImagePath
            )
            playlistInteractor.cretePlaylist(playlist)
            _navigationState.postValue(NavigationState.PlaylistCreated(playlist.name))
        }
    }

    fun btnBackHandler() {
        if (_name.value.isNullOrEmpty() && _description.value.isNullOrEmpty() && _selectedImageUri.value == null) {
            _navigationState.postValue(NavigationState.NavigateBack)
        } else {
            _navigationState.postValue(NavigationState.ShowDialogBeforeNavigateBack)
        }
    }

    fun resetNavigationState() {
        _navigationState.postValue(NavigationState.DefaultNothing)
    }

    fun setSelectedImageUri(uri: Uri) {
        _selectedImageUri.value = uri
    }

}