package com.refrii.client.di

import com.refrii.client.BoxInfoPresenterTest
import com.refrii.client.boxinfo.BoxInfoActivity
import com.refrii.client.boxinfo.BoxInfoPresenter
import com.refrii.client.food.FoodActivity
import com.refrii.client.foodlist.FoodListActivity
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
    TestAppModule::class,
    TestApiRepositoryModule::class])
interface TestAppComponent {
    fun inject(target: BoxInfoPresenterTest)
}