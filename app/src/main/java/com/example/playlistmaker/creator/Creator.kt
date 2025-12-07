package com.example.playlistmaker.creator

import com.example.playlistmaker.data.network.PlaylistsRepositoryImpl
import com.example.playlistmaker.data.network.SearchHistoryRepositoryImpl
import com.example.playlistmaker.data.network.TracksRepositoryImpl
import com.example.playlistmaker.domain.api.PlaylistsRepository
import com.example.playlistmaker.domain.api.SearchHistoryRepository
import com.example.playlistmaker.domain.api.TracksRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

object Creator {
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val databaseMock = DatabaseMock(applicationScope)

    fun provideTracksRepository(): TracksRepository {
        return TracksRepositoryImpl(databaseMock)
    }


    fun providePlaylistsRepository(): PlaylistsRepository {
        return PlaylistsRepositoryImpl(databaseMock)
    }

    fun provideSearchHistoryRepository(): SearchHistoryRepository {
        return SearchHistoryRepositoryImpl(databaseMock)
    }
}