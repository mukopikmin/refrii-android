package com.refrii.client.di

import com.refrii.client.boxinfo.BoxInfoActivity
import com.refrii.client.boxinfo.BoxInfoModule
import com.refrii.client.food.FoodActivity
import com.refrii.client.food.FoodModule
import com.refrii.client.foodlist.FoodListActivity
import com.refrii.client.foodlist.FoodListModule
import com.refrii.client.newfood.NewFoodActivity
import com.refrii.client.newfood.NewFoodModule
import com.refrii.client.newunit.NewUnitActivity
import com.refrii.client.newunit.NewUnitModule
import com.refrii.client.shopplans.ShopPlansActivity
import com.refrii.client.shopplans.ShopPlansModule
import com.refrii.client.signin.SignInActivity
import com.refrii.client.signin.SigninModule
import com.refrii.client.unit.UnitActivity
import com.refrii.client.unit.UnitModule
import com.refrii.client.unitlist.UnitListActivity
import com.refrii.client.unitlist.UnitListModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBindingModule {

    @ActivityScoped
    @ContributesAndroidInjector(modules = [(SigninModule::class)])
    abstract fun signinActivity(): SignInActivity

    @ActivityScoped
    @ContributesAndroidInjector(modules = [(FoodListModule::class)])
    abstract fun foodListActivity(): FoodListActivity

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
}
