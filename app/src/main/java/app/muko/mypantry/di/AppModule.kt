package app.muko.mypantry.di

import android.app.Application
import android.content.Context
import app.muko.mypantry.boxinfo.BoxInfoContract
import app.muko.mypantry.boxinfo.BoxInfoPresenter
import app.muko.mypantry.data.source.*
import app.muko.mypantry.food.FoodContract
import app.muko.mypantry.food.FoodPresenter
import app.muko.mypantry.foodlist.FoodListContract
import app.muko.mypantry.foodlist.FoodListPresenter
import app.muko.mypantry.invitations.InvitationListContract
import app.muko.mypantry.invitations.InvitationListPresenter
import app.muko.mypantry.newfood.NewFoodContract
import app.muko.mypantry.newfood.NewFoodPresenter
import app.muko.mypantry.newunit.NewUnitContract
import app.muko.mypantry.newunit.NewUnitPresenter
import app.muko.mypantry.noticelist.NoticeListContract
import app.muko.mypantry.noticelist.NoticeListPresenter
import app.muko.mypantry.settings.SettingsContract
import app.muko.mypantry.settings.SettingsPresenter
import app.muko.mypantry.shopplans.ShopPlansContract
import app.muko.mypantry.shopplans.ShopPlansPresenter
import app.muko.mypantry.signin.SigninContract
import app.muko.mypantry.signin.SigninPresenter
import app.muko.mypantry.unit.UnitContract
import app.muko.mypantry.unit.UnitPresenter
import app.muko.mypantry.unitlist.UnitListContract
import app.muko.mypantry.unitlist.UnitListPresenter
import dagger.Module
import dagger.Provides
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
    fun provideFoodPresenter(foodRepository: ApiFoodRepository, shopPlanRepository: ApiShopPlanRepository, apiUnitRepository: ApiUnitRepository): FoodContract.Presenter {
        return FoodPresenter(foodRepository, shopPlanRepository, apiUnitRepository)
    }

    @Provides
    fun provideNewFoodPresenter(boxRepository: ApiBoxRepository, foodRepository: ApiFoodRepository, unitRepository: ApiUnitRepository): NewFoodContract.Presenter {
        return NewFoodPresenter(boxRepository, foodRepository, unitRepository)
    }

    @Provides
    fun provideBoxInfoPresenter(boxRepository: ApiBoxRepository): BoxInfoContract.Presenter {
        return BoxInfoPresenter(boxRepository)
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
    fun provideNoticeListPresenter(foodRepository: ApiFoodRepository, noticeRepository: ApiNoticeRepository): NoticeListContract.Presenter {
        return NoticeListPresenter(foodRepository, noticeRepository)
    }

    @Provides
    fun provideInvitationListPresenter(boxRepository: ApiBoxRepository, invitationRepository: ApiInvitationRepository): InvitationListContract.Presenter {
        return InvitationListPresenter(boxRepository, invitationRepository)
    }

    @Provides
    fun provideSettingsPresenter(userRepository: ApiUserRepository): SettingsContract.Presenter {
        return SettingsPresenter(userRepository)
    }
}
