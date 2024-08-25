package com.example.playlistmaker.media.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.interactor.PlaylistInteractor
import com.example.playlistmaker.media.domain.model.Playlist
import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class EditPlaylistViewModel(private val playlistInteractor: PlaylistInteractor) : ViewModel() {

    private val _currentPlaylist = MutableLiveData<Playlist>()
    val currentPlaylist: LiveData<Playlist> get() = _currentPlaylist

    private val _playlistTracks = MutableLiveData<List<Track>>()
    val playlistTracks: LiveData<List<Track>> get() = _playlistTracks

    private val _tracksTime = MutableLiveData<Int>()
    val tracksTime: LiveData<Int> get() = _tracksTime

    private val _showEmptyPlaylistMessage = MutableLiveData(false)
    val showEmptyPlaylistMessage: LiveData<Boolean> get() = _showEmptyPlaylistMessage

    private val _navigateBack = MutableLiveData<Unit>()
    val navigateBack: LiveData<Unit> get() = _navigateBack

    private val _hideBottomSheetMenu = MutableLiveData<Unit>()
    val hideBottomSheetMenu: LiveData<Unit> get() = _hideBottomSheetMenu

    fun loadPlaylist(playlistId: Int) {
        viewModelScope.launch {
            val playlist = playlistInteractor.getPlaylistById(playlistId)
            val trackList = playlistInteractor.getAllTracksByIds(playlist.trackIdList)
            val totalDurationOfAllTracks =
                trackList?.fold(0) { acc, track -> acc + track.trackTimeMillis }
            val tracksTime =
                SimpleDateFormat("mm", Locale.getDefault()).format(totalDurationOfAllTracks)
                    .toIntOrNull() ?: 0
            _currentPlaylist.value = playlist
            _playlistTracks.value = if (trackList.isNullOrEmpty()) emptyList() else trackList
            _tracksTime.value = tracksTime
        }

    }

    fun removeTrackFromPlaylist(track: Track) {
        viewModelScope.launch {
            _currentPlaylist.value?.id?.let {
                playlistInteractor.removeTrackFromPlaylist(
                    it,
                    track.trackId
                )
            }
            _currentPlaylist.value?.id?.let { loadPlaylist(it) }
        }
    }


    fun sharePlaylist() {
        _currentPlaylist.value?.let {
            if (it.tracksCount > 0) {
                viewModelScope.launch {
                    playlistInteractor.sharePlaylist(it.id)
                    _hideBottomSheetMenu.postValue(Unit)
                }
            } else {
                showNoTracksMessage(true)
                _hideBottomSheetMenu.postValue(Unit)
            }
        }

    }

    fun showNoTracksMessage(status: Boolean) {
        _showEmptyPlaylistMessage.postValue(status)
    }

    fun deletePlaylist() {
        viewModelScope.launch {
            _currentPlaylist.value?.id?.let { playlistInteractor.deletePlaylist(it) }
            _navigateBack.value = Unit
        }
    }
}
