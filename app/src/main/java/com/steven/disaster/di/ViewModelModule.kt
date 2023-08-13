package com.steven.disaster.di

import com.steven.disaster.data.ApiService
import com.steven.disaster.viewmodel.MainViewModel
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
    fun provideMainViewModel(apiService: ApiService): MainViewModel = MainViewModel(apiService)
}