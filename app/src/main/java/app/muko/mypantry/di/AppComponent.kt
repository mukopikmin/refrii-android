package app.muko.mypantry.di

import app.muko.mypantry.boxinfo.BoxInfoActivity
import app.muko.mypantry.food.FoodActivity
import app.muko.mypantry.foodlist.FoodListActivity
import app.muko.mypantry.invitations.InvitationListActivity
import app.muko.mypantry.newfood.NewFoodActivity
import app.muko.mypantry.newunit.NewUnitActivity
import app.muko.mypantry.noticelist.NoticeListActivity
import app.muko.mypantry.notification.PushNotificationService
import app.muko.mypantry.settings.SettingsActivity
import app.muko.mypantry.shopplans.ShopPlansActivity
import app.muko.mypantry.signin.SignInActivity
import app.muko.mypantry.unit.UnitActivity
import app.muko.mypantry.unitlist.UnitListActivity
import dagger.Component
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [
    AppModule::class,
    RetrofitModule::class,
    ApiRepositoryModule::class,
    ActivityBindingModule::class,
    AndroidSupportInjectionModule::class])
interface AppComponent {
    fun inject(target: SignInActivity)
    fun inject(target: FoodListActivity)
    fun inject(target: FoodActivity)
    fun inject(target: NewFoodActivity)
    fun inject(target: BoxInfoActivity)
    fun inject(target: UnitListActivity)
    fun inject(target: UnitActivity)
    fun inject(target: NewUnitActivity)
    fun inject(target: ShopPlansActivity)
    fun inject(target: SettingsActivity.SettingsFragment)
    fun inject(target: PushNotificationService)
    fun inject(target: NoticeListActivity)
    fun inject(target: InvitationListActivity)
}