package com.steven.disaster.viewmodel

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.steven.disaster.data.SettingPreference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(dataStore: DataStore<Preferences>) :
    ViewModel() {
    private val pref = SettingPreference(dataStore)

    fun getThemeSetting(): LiveData<Boolean> {
        return pref.getTheme().asLiveData()
    }

    fun saveThemeSetting(isDarkModeActive: Boolean) {
        viewModelScope.launch {
            pref.saveTheme(isDarkModeActive)
        }
    }
}