package app.muko.mypantry.di

import app.muko.mypantry.data.dao.BoxDao
import app.muko.mypantry.data.dao.FoodDao
import app.muko.mypantry.data.dao.ShopPlanDao
import app.muko.mypantry.data.dao.UnitDao
import app.muko.mypantry.data.source.*
import app.muko.mypantry.data.source.remote.services.BoxService
import app.muko.mypantry.data.source.remote.services.FoodService
import app.muko.mypantry.data.source.remote.services.ShopPlanService
import app.muko.mypantry.data.source.remote.services.UnitService
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
class ApiRepositoryModule {

    @Singleton
    @Provides
    fun provideApiBoxRepository(service: BoxService, dao: BoxDao): ApiBoxRepository {
        return ApiBoxRepository(service, dao)
    }

    @Singleton
    @Provides
    fun provideApiFoodRepository(service: FoodService, dao: FoodDao): ApiFoodRepository {
        return ApiFoodRepository(service, dao)
    }

    @Singleton
    @Provides
    fun provideApiShopPlanRepository(service: ShopPlanService, dao: ShopPlanDao): ApiShopPlanRepository {
        return ApiShopPlanRepository(service, dao)
    }

    @Singleton
    @Provides
    fun provideApiUnitRepository(service: UnitService, dao: UnitDao): ApiUnitRepository {
        return ApiUnitRepository(service, dao)
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