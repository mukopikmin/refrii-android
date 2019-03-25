package com.refrii.client.di

import com.refrii.client.boxinfo.BoxInfoActivity
import com.refrii.client.food.FoodActivity
import com.refrii.client.foodlist.FoodListActivity
import com.refrii.client.newbox.NewBoxActivity
import com.refrii.client.newfood.NewFoodActivity
import com.refrii.client.newunit.NewUnitActivity
import com.refrii.client.signin.SignInActivity
import com.refrii.client.unit.UnitActivity
import com.refrii.client.unitlist.UnitListActivity
import dagger.Component
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [
    AppModule::class,
    ApiRepositoryModule::class,
    ActivityBindingModule::class,
    AndroidSupportInjectionModule::class])
interface AppComponent {
    fun inject(target: SignInActivity)
    fun inject(target: FoodListActivity)
    fun inject(target: NewBoxActivity)
    fun inject(target: FoodActivity)
    fun inject(target: NewFoodActivity)
    fun inject(target: BoxInfoActivity)
    fun inject(target: UnitListActivity)
    fun inject(target: UnitActivity)
    fun inject(target: NewUnitActivity)
}