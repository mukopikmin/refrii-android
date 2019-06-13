package com.refrii.client.di

import com.refrii.client.data.source.*
import dagger.Module
import dagger.Provides
import io.realm.Realm
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
class ApiRepositoryModule {

    @Singleton
    @Provides
    fun provideApiBoxRepository(realm: Realm, retrofit: Retrofit): ApiBoxRepository {
        return ApiBoxRepository(realm, retrofit)
    }

    @Singleton
    @Provides
    fun provideApiFoodRepository(realm: Realm, retrofit: Retrofit): ApiFoodRepository {
        return ApiFoodRepository(realm, retrofit)
    }

    @Singleton
    @Provides
    fun provideApiShopPlanRepository(realm: Realm, retrofit: Retrofit): ApiShopPlanRepository {
        return ApiShopPlanRepository(realm, retrofit)
    }

    @Singleton
    @Provides
    fun provideApiUnitRepository(realm: Realm, retrofit: Retrofit): ApiUnitRepository {
        return ApiUnitRepository(realm, retrofit)
    }

    @Singleton
    @Provides
    fun provideApiUserRepository(realm: Realm, retrofit: Retrofit): ApiUserRepository {
        return ApiUserRepository(realm, retrofit)
    }
}