package com.example.playlistmaker.data.network

import com.example.playlistmaker.creator.DatabaseMock
import com.example.playlistmaker.domain.api.SearchHistoryRepository
import com.example.playlistmaker.domain.models.Word
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class SearchHistoryRepositoryImpl(scope: CoroutineScope) : SearchHistoryRepository {

    private val dbMock = DatabaseMock(scope)

    override fun getHistoryRequests(): Flow<List<Word>> = flow {
        emit(dbMock.getHistoryRequests())

        dbMock.historyUpdates.collect {
            emit(dbMock.getHistoryRequests())
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun addToHistory(word: Word) = withContext(Dispatchers.IO) {
        dbMock.addToHistory(word)
    }
}