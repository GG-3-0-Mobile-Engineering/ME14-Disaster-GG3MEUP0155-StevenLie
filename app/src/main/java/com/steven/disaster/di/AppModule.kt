package com.steven.disaster.di

import android.app.Application
import androidx.work.WorkManager
import com.steven.disaster.utils.WaterLevelNotification
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideWaterLevelNotification(context: Application): WaterLevelNotification =
        WaterLevelNotification(context)

    @Provides
    @Singleton
    fun provideWorkManager(context: Application): WorkManager = WorkManager.getInstance(context)
}