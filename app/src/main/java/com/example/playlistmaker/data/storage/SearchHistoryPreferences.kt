package com.example.playlistmaker.data.storage

import android.content.Context
import android.content.SharedPreferences
import com.example.playlistmaker.domain.models.Word
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistoryPreferences(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(HISTORY_PREFS, Context.MODE_PRIVATE)
    private val gson = Gson()
    private val key = "history_list"

    fun getHistory(): List<Word> {
        val json = sharedPreferences.getString(key, null)
        return if (json != null) {
            val type = object : TypeToken<List<Word>>() {}.type
            gson.fromJson(json, type)
        } else {
            emptyList()
        }
    }

    fun addEntry(word: Word) {
        val currentHistory = getHistory().toMutableList()

        currentHistory.removeAll { it.word == word.word }

        currentHistory.add(0, word)

        if (currentHistory.size > MAX_HISTORY_SIZE) {

            val trimmedList = currentHistory.subList(0, MAX_HISTORY_SIZE)
            currentHistory.clear()
            currentHistory.addAll(trimmedList)
        }

        saveList(currentHistory)
    }

    fun clear() {
        sharedPreferences.edit().remove(key).apply()
    }

    private fun saveList(history: List<Word>) {
        val json = gson.toJson(history)
        sharedPreferences.edit()
            .putString(key, json)
            .apply()
    }

    companion object {
        private const val HISTORY_PREFS = "playlist_maker_history"
        private const val MAX_HISTORY_SIZE = 10
    }
}