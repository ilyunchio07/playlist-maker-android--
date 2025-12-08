package com.example.playlistmaker.creator

import android.content.Context
import androidx.room.Room
import com.example.playlistmaker.data.converters.TrackDbConverter
import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.network.PlaylistsRepositoryImpl
import com.example.playlistmaker.data.network.SearchHistoryRepositoryImpl
import com.example.playlistmaker.data.network.TracksRepositoryImpl
import com.example.playlistmaker.data.storage.SearchHistoryPreferences // Импорт нового класса
import com.example.playlistmaker.domain.api.PlaylistsRepository
import com.example.playlistmaker.domain.api.SearchHistoryRepository
import com.example.playlistmaker.domain.api.TracksRepository

object Creator {
    private var applicationContext: Context? = null
    private var database: AppDatabase? = null

    // Переменная для хранения нашего класса с SharedPreferences
    private var searchHistoryPreferences: SearchHistoryPreferences? = null

    fun initialize(context: Context) {
        applicationContext = context

        // Инициализируем хранилище истории
        searchHistoryPreferences = SearchHistoryPreferences(context)

        database = Room.databaseBuilder(context, AppDatabase::class.java, "database.db")
            .fallbackToDestructiveMigration()
            .build()
    }

    // ... методы provideTracksRepository и providePlaylistsRepository без изменений ...
    fun provideTracksRepository(): TracksRepository {
        return TracksRepositoryImpl(getDatabase(), TrackDbConverter())
    }

    fun providePlaylistsRepository(): PlaylistsRepository {
        return PlaylistsRepositoryImpl(getDatabase(), TrackDbConverter(), applicationContext!!)
    }

    // ОБНОВЛЕННЫЙ МЕТОД
    fun provideSearchHistoryRepository(): SearchHistoryRepository {
        return SearchHistoryRepositoryImpl(getHistoryPreferences())
    }

    // Вспомогательный метод
    private fun getDatabase(): AppDatabase {
        return database ?: throw IllegalStateException("Database not initialized!")
    }

    // Вспомогательный метод для Preferences
    private fun getHistoryPreferences(): SearchHistoryPreferences {
        return searchHistoryPreferences ?: throw IllegalStateException("Preferences not initialized!")
    }
}