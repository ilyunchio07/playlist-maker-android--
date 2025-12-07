package com.example.playlistmaker.data.network

import com.example.playlistmaker.creator.DatabaseMock
import com.example.playlistmaker.domain.api.SearchHistoryRepository
import com.example.playlistmaker.domain.models.Word
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SearchHistoryRepositoryImpl(
    private val database: DatabaseMock // Принимаем базу
) : SearchHistoryRepository {

    override fun getHistoryRequests(): Flow<List<Word>> {
        // Превращаем SharedFlow<Unit> в Flow<List<Word>>
        // Сначала эмитим текущее состояние, потом слушаем обновления
        return kotlinx.coroutines.flow.flow {
            emit(database.getHistoryRequests())
            database.historyUpdates.collect {
                emit(database.getHistoryRequests())
            }
        }
    }

    override suspend fun addToHistory(word: Word) {
        database.addToHistory(word)
    }

    override suspend fun clearHistory() {
        database.clearHistory()
    }
}