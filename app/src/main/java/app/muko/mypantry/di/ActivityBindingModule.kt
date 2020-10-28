package app.muko.mypantry.di

import app.muko.mypantry.boxinfo.BoxInfoActivity
import app.muko.mypantry.boxinfo.BoxInfoModule
import app.muko.mypantry.food.FoodActivity
import app.muko.mypantry.food.FoodModule
import app.muko.mypantry.foodlist.FoodListActivity
import app.muko.mypantry.fragments.foodlist.FoodListFragment
import app.muko.mypantry.foodlist.FoodListModule
import app.muko.mypantry.fragments.foodlist.FoodListViewModelModule
import app.muko.mypantry.fragments.navigation.FoodActionDialogFragment
import app.muko.mypantry.fragments.navigation.FoodActionDialogViewModel
import app.muko.mypantry.fragments.navigation.FoodActionDialogViewModelModule
import app.muko.mypantry.fragments.signin.SigninFragment
import app.muko.mypantry.fragments.signin.SigninViewModelModule
import app.muko.mypantry.invitations.InvitationListActivity
import app.muko.mypantry.invitations.InvitationListModule
import app.muko.mypantry.newfood.NewFoodActivity
import app.muko.mypantry.newfood.NewFoodModule
import app.muko.mypantry.newunit.NewUnitActivity
import app.muko.mypantry.newunit.NewUnitModule
import app.muko.mypantry.noticelist.NoticeListActivity
import app.muko.mypantry.noticelist.NoticeListModule
import app.muko.mypantry.settings.SettingsActivity
import app.muko.mypantry.settings.SettingsModule
import app.muko.mypantry.shopplans.ShopPlansActivity
import app.muko.mypantry.shopplans.ShopPlansModule
import app.muko.mypantry.signin.SignInActivity
import app.muko.mypantry.signin.SigninModule
import app.muko.mypantry.unit.UnitActivity
import app.muko.mypantry.unit.UnitModule
import app.muko.mypantry.unitlist.UnitListActivity
import app.muko.mypantry.unitlist.UnitListModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBindingModule {

    @ActivityScoped
    @ContributesAndroidInjector(modules = [(SigninModule::class)])
    abstract fun signinActivity(): SignInActivity

    @ActivityScoped
    @ContributesAndroidInjector(modules = [
        FoodListModule::class
    ])
    abstract fun foodListActivity(): FoodListActivity

    @ActivityScoped
    @ContributesAndroidInjector(modules = [
        FoodListViewModelModule::class
    ])
    abstract fun foodListFragment(): FoodListFragment

    @ActivityScoped
    @ContributesAndroidInjector(modules = [
        FoodActionDialogViewModelModule::class
    ])
    abstract fun foodActionDialogFragment(): FoodActionDialogFragment

    @ActivityScoped
    @ContributesAndroidInjector(modules = [
        SigninViewModelModule::class
    ])
    abstract fun signinFragment(): SigninFragment

    @ActivityScoped
    @ContributesAndroidInjector(modules = [(FoodModule::class)])
    abstract fun foodActivity(): FoodActivity

    @ActivityScoped
    @ContributesAndroidInjector(modules = [(NewFoodModule::class)])
    abstract fun newFoodActivity(): NewFoodActivity

    @ActivityScoped
    @ContributesAndroidInjector(modules = [(BoxInfoModule::class)])
    abstract fun boxInfoActivity(): BoxInfoActivity

    @ActivityScoped
    @ContributesAndroidInjector(modules = [(UnitListModule::class)])
    abstract fun unitListActivity(): UnitListActivity

    @ActivityScoped
    @ContributesAndroidInjector(modules = [(UnitModule::class)])
    abstract fun unitActivity(): UnitActivity

    @ActivityScoped
    @ContributesAndroidInjector(modules = [(NewUnitModule::class)])
    abstract fun newUnitActivity(): NewUnitActivity

    @ActivityScoped
    @ContributesAndroidInjector(modules = [(ShopPlansModule::class)])
    abstract fun shopPlansActivity(): ShopPlansActivity

    @ActivityScoped
    @ContributesAndroidInjector(modules = [(NoticeListModule::class)])
    abstract fun noticeListActivity(): NoticeListActivity

    @ActivityScoped
    @ContributesAndroidInjector(modules = [(SettingsModule::class)])
    abstract fun settingsFragment(): SettingsActivity.SettingsFragment

    @ActivityScoped
    @ContributesAndroidInjector(modules = [(InvitationListModule::class)])
    abstract fun invitationListActivity(): InvitationListActivity
}
