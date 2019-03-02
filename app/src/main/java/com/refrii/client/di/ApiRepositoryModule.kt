package com.refrii.client.di

import com.refrii.client.data.api.source.ApiRepository
import dagger.Module
import dagger.Provides
import io.realm.Realm
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
class ApiRepositoryModule {

    @Singleton
    @Provides
    fun provideApiRepository(realm: Realm, retrofit: Retrofit): ApiRepository {
        return ApiRepository(realm, retrofit)
    }
}