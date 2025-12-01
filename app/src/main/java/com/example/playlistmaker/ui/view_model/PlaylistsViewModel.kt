package com.example.playlistmaker.ui.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.domain.api.PlaylistsRepository
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class PlaylistsViewModel(
    private val playlistsRepository: PlaylistsRepository,
    private val tracksRepository: TracksRepository
) : ViewModel() {

    val playlists: Flow<List<Playlist>> = playlistsRepository.getAllPlaylists()

    val favoriteList: Flow<List<Track>> = tracksRepository.getFavoriteTracks()

    fun createNewPlaylist(name: String, description: String) {
        viewModelScope.launch(Dispatchers.IO) {
            playlistsRepository.addNewPlaylist(name, description)
        }
    }

    fun insertTrackToPlaylist(track: Track, playlistId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            tracksRepository.insertTrackToPlaylist(track, playlistId)
        }
    }

    fun toggleFavorite(track: Track) {
        viewModelScope.launch(Dispatchers.IO) {
            tracksRepository.updateTrackFavoriteStatus(track, !track.isFavorite)
        }
    }

    fun deleteTrackFromPlaylist(track: Track) {
        viewModelScope.launch(Dispatchers.IO) {
            tracksRepository.deleteTrackFromPlaylist(track)
        }
    }

    fun deletePlaylistById(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            tracksRepository.deleteTracksByPlaylistId(id)
            playlistsRepository.deletePlaylistById(id)
        }
    }

    suspend fun isTrackExist(track: Track): Boolean {
        return tracksRepository.getTrackByNameAndArtist(track).firstOrNull() != null
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                PlaylistsViewModel(
                    playlistsRepository = Creator.providePlaylistsRepository(),
                    tracksRepository = Creator.provideTracksRepository()
                )
            }
        }
    }
}