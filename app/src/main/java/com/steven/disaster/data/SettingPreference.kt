package com.steven.disaster.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingPreference(private val dataStore: DataStore<Preferences>) {
    private val isDarkMode = booleanPreferencesKey("dark_mode")

    fun getTheme(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[isDarkMode] ?: false
        }
    }

    suspend fun saveTheme(isDarkModeActive: Boolean) {
        dataStore.edit { preferences ->
            preferences[isDarkMode] = isDarkModeActive
        }
    }
}