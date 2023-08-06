package com.steven.disaster.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import com.steven.disaster.data.SettingPreference
import com.steven.disaster.viewmodel.SettingViewModel
import com.steven.disaster.databinding.ActivitySettingsBinding
import com.steven.disaster.data.prefDataStore

class SettingsActivity : AppCompatActivity() {
    private lateinit var settingsBinding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        settingsBinding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(settingsBinding.root)

        setSupportActionBar(settingsBinding.toolbarSetting)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val pref = SettingPreference.getInstance(application.prefDataStore)
        val settingViewModel = ViewModelProvider(this)[SettingViewModel::class.java]
        settingViewModel.setPref(pref)

        settingViewModel.getThemeSetting().observe(this) { isDarkMode ->
            if (isDarkMode) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                settingsBinding.switchDarkMode.isChecked = true
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                settingsBinding.switchDarkMode.isChecked = false
            }
        }

        settingsBinding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            settingViewModel.saveThemeSetting(isChecked)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}