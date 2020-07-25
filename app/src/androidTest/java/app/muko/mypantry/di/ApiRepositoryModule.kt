package app.muko.mypantry.di

import android.content.Context
import app.muko.mypantry.data.dao.LocalDatabase
import app.muko.mypantry.data.source.*
import app.muko.mypantry.data.source.remote.services.BoxService
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
class ApiRepositoryModule {

    @Singleton
    @Provides
    fun provideApiBoxRepository(boxService: BoxService, room: LocalDatabase): ApiBoxRepository {
        return ApiBoxRepository(boxService, room)
    }

    @Singleton
    @Provides
    fun provideApiFoodRepository(retrofit: Retrofit, context: Context): ApiFoodRepository {
        return ApiFoodRepository(context, retrofit)
    }

    @Singleton
    @Provides
    fun provideApiShopPlanRepository(retrofit: Retrofit): ApiShopPlanRepository {
        return ApiShopPlanRepository(retrofit)
    }

    @Singleton
    @Provides
    fun provideApiUnitRepository(retrofit: Retrofit): ApiUnitRepository {
        return ApiUnitRepository(retrofit)
    }

    @Singleton
    @Provides
    fun provideApiUserRepository(retrofit: Retrofit): ApiUserRepository {
        return ApiUserRepository(retrofit)
    }

    @Singleton
    @Provides
    fun provideApiNoticeRepository(retrofit: Retrofit): ApiNoticeRepository {
        return ApiNoticeRepository(retrofit)
    }

    @Singleton
    @Provides
    fun provideApiInvitationRepository(retrofit: Retrofit): ApiInvitationRepository {
        return ApiInvitationRepository(retrofit)
    }
}