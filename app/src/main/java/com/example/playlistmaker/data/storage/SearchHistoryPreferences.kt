package com.example.playlistmaker.data.storage

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class SearchHistoryPreferences(
    private val dataStore: DataStore<Preferences>
) {
    private val PREFERENCES_KEY = stringPreferencesKey("search_history_key")
    private val MAX_ENTRIES = 10
    private val SEPARATOR = ","

    val historyFlow: Flow<List<String>> = dataStore.data.map { preferences ->
        val historyString = preferences[PREFERENCES_KEY].orEmpty()
        if (historyString.isEmpty()) {
            emptyList()
        } else {
            historyString.split(SEPARATOR)
        }
    }

    suspend fun addEntry(word: String) {
        if (word.isEmpty()) return

        dataStore.edit { preferences ->
            val historyString = preferences[PREFERENCES_KEY].orEmpty()
            val history = if (historyString.isNotEmpty()) {
                historyString.split(SEPARATOR).toMutableList()
            } else {
                mutableListOf()
            }

            history.remove(word)
            history.add(0, word)

            val subList = if (history.size > MAX_ENTRIES) history.subList(0, MAX_ENTRIES) else history

            val updatedString = subList.joinToString(SEPARATOR)
            preferences[PREFERENCES_KEY] = updatedString
        }
    }

    suspend fun getEntries(): List<String> {
        return dataStore.data.map { preferences ->
            val historyString = preferences[PREFERENCES_KEY].orEmpty()
            if (historyString.isEmpty()) {
                emptyList()
            } else {
                historyString.split(SEPARATOR)
            }
        }.first()
    }

    suspend fun clear() {
        dataStore.edit { preferences ->
            preferences.remove(PREFERENCES_KEY)
        }
    }
}
