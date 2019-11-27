package com.refrii.client.di

import android.app.Application
import android.content.Context
import com.refrii.client.boxinfo.BoxInfoContract
import com.refrii.client.boxinfo.BoxInfoPresenter
import com.refrii.client.data.source.*
import com.refrii.client.food.FoodContract
import com.refrii.client.food.FoodPresenter
import com.refrii.client.foodlist.FoodListContract
import com.refrii.client.foodlist.FoodListPresenter
import com.refrii.client.newfood.NewFoodContract
import com.refrii.client.newfood.NewFoodPresenter
import com.refrii.client.newunit.NewUnitContract
import com.refrii.client.newunit.NewUnitPresenter
import com.refrii.client.noticelist.NoticeListContract
import com.refrii.client.noticelist.NoticeListPresenter
import com.refrii.client.settings.SettingsContract
import com.refrii.client.settings.SettingsPresenter
import com.refrii.client.shopplans.ShopPlansContract
import com.refrii.client.shopplans.ShopPlansPresenter
import com.refrii.client.signin.SigninContract
import com.refrii.client.signin.SigninPresenter
import com.refrii.client.unit.UnitContract
import com.refrii.client.unit.UnitPresenter
import com.refrii.client.unitlist.UnitListContract
import com.refrii.client.unitlist.UnitListPresenter
import com.refrii.client.welcome.WelcomeContract
import com.refrii.client.welcome.WelcomePresenter
import dagger.Module
import dagger.Provides
import io.realm.Realm
import io.realm.RealmConfiguration
import javax.inject.Singleton


@Module
class AppModule(private var mApplication: Application) {

    @Singleton
    @Provides
    fun provideApplicationContext(): Context {
        return mApplication.applicationContext
    }

    @Provides
    fun provideSigninPresenter(apiUserRepository: ApiUserRepository): SigninContract.Presenter {
        return SigninPresenter(apiUserRepository)
    }

    @Provides
    fun provideFoodListPresenter(boxRepository: ApiBoxRepository, foodRepository: ApiFoodRepository, userRepository: ApiUserRepository): FoodListContract.Presenter {
        return FoodListPresenter(boxRepository, foodRepository, userRepository)
    }

    @Provides
    fun provideFoodPresenter(foodRepository: ApiFoodRepository, boxRepository: ApiBoxRepository, shopPlanRepository: ApiShopPlanRepository): FoodContract.Presenter {
        return FoodPresenter(foodRepository, boxRepository, shopPlanRepository)
    }

    @Provides
    fun provideNewFoodPresenter(boxRepository: ApiBoxRepository, foodRepository: ApiFoodRepository): NewFoodContract.Presenter {
        return NewFoodPresenter(boxRepository, foodRepository)
    }

    @Provides
    fun provideBoxInfoPresenter(boxRepository: ApiBoxRepository, invitationRepository: ApiInvitationRepository): BoxInfoContract.Presenter {
        return BoxInfoPresenter(boxRepository, invitationRepository)
    }

    @Provides
    fun provideUnitListPresenter(unitRepository: ApiUnitRepository): UnitListContract.Presenter {
        return UnitListPresenter(unitRepository)
    }

    @Provides
    fun provideUnitPresenter(unitRepository: ApiUnitRepository): UnitContract.Presenter {
        return UnitPresenter(unitRepository)
    }

    @Provides
    fun provideNewUnitPresenter(unitRepository: ApiUnitRepository): NewUnitContract.Presenter {
        return NewUnitPresenter(unitRepository)
    }

    @Provides
    fun provideShopPlansPresenter(shopPlanRepository: ApiShopPlanRepository): ShopPlansContract.Presenter {
        return ShopPlansPresenter(shopPlanRepository)
    }

    @Provides
    fun provideWelcomePresenter(boxRepository: ApiBoxRepository): WelcomeContract.Presenter {
        return WelcomePresenter(boxRepository)
    }

    @Provides
    fun provideNoticeListPresenter(foodRepository: ApiFoodRepository, noticeRepository: ApiNoticeRepository): NoticeListContract.Presenter {
        return NoticeListPresenter(foodRepository, noticeRepository)
    }

    @Provides
    fun provideSettingsPresenter(userRepository: ApiUserRepository): SettingsContract.Presenter {
        return SettingsPresenter(userRepository)
    }

    @Singleton
    @Provides
    fun provideRealm(): Realm {
        Realm.init(provideApplicationContext())
        Realm.setDefaultConfiguration(RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build())

        return Realm.getDefaultInstance()
    }
}
