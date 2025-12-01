package com.example.playlistmaker.ui.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow

class FavoritesViewModel(
    tracksRepository: TracksRepository
) : ViewModel() {

    val favoriteTracks: Flow<List<Track>> = tracksRepository.getFavoriteTracks()

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                FavoritesViewModel(
                    tracksRepository = Creator.provideTracksRepository()
                )
            }
        }
    }
}