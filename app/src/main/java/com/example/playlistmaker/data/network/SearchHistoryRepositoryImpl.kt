package com.example.playlistmaker.data.network

import com.example.playlistmaker.data.storage.SearchHistoryPreferences
import com.example.playlistmaker.domain.api.SearchHistoryRepository
import com.example.playlistmaker.domain.models.Word
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SearchHistoryRepositoryImpl(
    private val historyPreferences: SearchHistoryPreferences
) : SearchHistoryRepository {

    override fun getHistoryRequests(): Flow<List<Word>> =
        historyPreferences.historyFlow.map { history ->
            history.map { text -> Word(word = text) }
        }

    override suspend fun addToHistory(word: Word) {
        historyPreferences.addEntry(word.word)
    }

    override suspend fun clearHistory() {
        historyPreferences.clear()
    }
}
