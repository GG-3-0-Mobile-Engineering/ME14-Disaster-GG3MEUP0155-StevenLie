package com.steven.disaster.di

import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.steven.disaster.data.ApiService
import com.steven.disaster.viewmodel.MainViewModel
import com.steven.disaster.viewmodel.SettingViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ViewModelModule {

    @Provides
    @Singleton
    fun provideMainViewModel(apiService: ApiService, context: Application): MainViewModel =
        MainViewModel(apiService, context)

    @Provides
    @Singleton
    fun provideSettingViewModel(dataStore: DataStore<Preferences>): SettingViewModel =
        SettingViewModel(dataStore)
}