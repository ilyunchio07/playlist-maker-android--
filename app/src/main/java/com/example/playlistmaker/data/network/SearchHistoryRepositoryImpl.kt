package com.example.playlistmaker.data.network

import com.example.playlistmaker.data.storage.SearchHistoryPreferences
import com.example.playlistmaker.domain.api.SearchHistoryRepository
import com.example.playlistmaker.domain.models.Word
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SearchHistoryRepositoryImpl(
    private val historyPreferences: SearchHistoryPreferences
) : SearchHistoryRepository {

    override fun getHistoryRequests(): Flow<List<Word>> = flow {
        emit(historyPreferences.getHistory())
    }

    override suspend fun addToHistory(word: Word) {
        historyPreferences.addEntry(word)
    }

    override suspend fun clearHistory() {
        historyPreferences.clear()
    }
}