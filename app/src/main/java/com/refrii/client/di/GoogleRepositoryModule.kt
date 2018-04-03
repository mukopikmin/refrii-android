package com.refrii.client.di

import android.content.Context
import com.refrii.client.data.google.GoogleRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class GoogleRepositoryModule {

    @Singleton
    @Provides
    fun provideGoogleRepository(context: Context): GoogleRepository {
        return GoogleRepository(context)
    }
}