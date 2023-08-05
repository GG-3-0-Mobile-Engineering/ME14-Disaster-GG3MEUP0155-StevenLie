package com.steven.disaster.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.steven.disaster.data.SettingPreference
import kotlinx.coroutines.launch

class SettingViewModel : ViewModel() {
    private lateinit var pref: SettingPreference

    fun setPref(pref: SettingPreference) {
        this.pref = pref
    }

    fun getThemeSetting(): LiveData<Boolean> {
        return pref.getTheme().asLiveData()
    }

    fun saveThemeSetting(isDarkModeActive: Boolean) {
        viewModelScope.launch {
            pref.saveTheme(isDarkModeActive)
        }
    }
}