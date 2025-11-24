package com.example.playlistmaker.creator

import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.data.network.TracksRepositoryImpl
import com.example.playlistmaker.domain.api.TracksRepository

object Creator {
    private fun getStorage(): Storage {
        return Storage()
    }

    fun provideTracksRepository(): TracksRepository {
        return TracksRepositoryImpl(RetrofitNetworkClient(getStorage()))
    }
}