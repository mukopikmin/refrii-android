package com.refrii.client.di

import android.content.Context
import com.refrii.client.data.api.source.ApiRepository
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
class ApiRepositoryModule {

    @Singleton
    @Provides
    fun provideApiRepository(context: Context, retrofit: Retrofit): ApiRepository {
        return ApiRepository(context, retrofit)
    }
}