package com.example.playlistmaker.creator

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import com.example.playlistmaker.data.converters.TrackDbConverter
import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.network.PlaylistsRepositoryImpl
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.data.network.SearchHistoryRepositoryImpl
import com.example.playlistmaker.data.network.TracksRepositoryImpl
import com.example.playlistmaker.data.storage.SearchHistoryPreferences
import com.example.playlistmaker.domain.api.PlaylistsRepository
import com.example.playlistmaker.domain.api.SearchHistoryRepository
import com.example.playlistmaker.domain.api.TracksRepository

private val Context.dataStore by preferencesDataStore(name = "search_history")

object Creator {

    private fun getDatabase(context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    fun provideTracksRepository(context: Context): TracksRepository {
        return TracksRepositoryImpl(
            networkClient = RetrofitNetworkClient(),
            appDatabase = getDatabase(context),
            trackDbConverter = TrackDbConverter()
        )
    }

    fun providePlaylistsRepository(context: Context): PlaylistsRepository {
        return PlaylistsRepositoryImpl(
            appDatabase = getDatabase(context),
            trackDbConverter = TrackDbConverter(),
            context = context
        )
    }

    fun provideSearchHistoryRepository(context: Context): SearchHistoryRepository {
        return SearchHistoryRepositoryImpl(
            historyPreferences = provideSearchHistoryPreferences(context)
        )
    }

    fun provideSearchHistoryPreferences(context: Context): SearchHistoryPreferences {
        return SearchHistoryPreferences(context.dataStore)
    }
}
