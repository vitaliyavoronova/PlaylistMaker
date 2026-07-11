package com.example.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistory (val sharedPreferences: SharedPreferences) {
    private val gson = Gson()
    fun saveTrack(track: Track){
        val history = getHistory()
        val existingIndex = history.indexOfFirst { it.trackId == track.trackId }

        val updatedHistory = if (existingIndex != -1) {
            history.toMutableList().apply { removeAt(existingIndex) }
        } else {
            history.toMutableList()
        }

        updatedHistory.add(0, track)

        if (updatedHistory.size > 10) {
            updatedHistory.removeAt(updatedHistory.lastIndex)
        }

        sharedPreferences.edit()
            .putString("search_history", gson.toJson(updatedHistory))
            .apply()

    }

    fun getHistory(): List<Track> {
        val key = "search_history"
        val jsonString = sharedPreferences.getString(key, null)

        return if (jsonString != null) {
            gson.fromJson(jsonString, object : TypeToken<ArrayList<Track>>() {}.type)
                ?: emptyList()
        } else {
            emptyList()
        }
    }

    fun clearHistory() {
        sharedPreferences.edit()
            .remove("search_history")
            .apply()
    }
}