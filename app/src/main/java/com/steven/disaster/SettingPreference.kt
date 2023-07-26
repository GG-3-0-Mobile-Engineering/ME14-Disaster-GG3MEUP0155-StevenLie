package com.steven.disaster

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.prefDataStore: DataStore<Preferences> by preferencesDataStore("settings")

class SettingPreference private constructor(private val dataStore: DataStore<Preferences>) {
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

    companion object {
        private var INSTANCE: SettingPreference? = null

        fun getInstance(dataStore: DataStore<Preferences>): SettingPreference {
            return INSTANCE ?: synchronized(this) {
                val instance = SettingPreference(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}