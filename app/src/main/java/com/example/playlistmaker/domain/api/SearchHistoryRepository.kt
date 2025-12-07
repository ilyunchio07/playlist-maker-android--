package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Word
import kotlinx.coroutines.flow.Flow

interface SearchHistoryRepository {
    fun getHistoryRequests(): Flow<List<Word>>
    suspend fun addToHistory(word: Word)
    suspend fun clearHistory()
}