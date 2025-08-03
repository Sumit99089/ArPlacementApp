package com.example.arplacementapp.di

import com.example.arplacementapp.data.repository.DrillRepository
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
    fun provideDrillRepository(): DrillRepository {
        return DrillRepository()
    }
}