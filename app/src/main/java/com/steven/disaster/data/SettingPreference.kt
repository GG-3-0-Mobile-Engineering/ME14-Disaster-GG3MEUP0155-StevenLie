package com.steven.disaster.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.prefDataStore: DataStore<Preferences> by preferencesDataStore(name = "THEME")

class SettingPreference(context: Context) {
    private val isDarkMode = booleanPreferencesKey("dark_mode")
    private val dataStore = context.prefDataStore

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