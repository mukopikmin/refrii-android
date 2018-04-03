package com.refrii.client.foodlist

import com.refrii.client.di.ActivityScoped
import dagger.Binds
import dagger.Module

@Module
abstract class FoodListModule {

    @ActivityScoped
    @Binds
    abstract fun providePresenter(presenter: FoodListPresenter): FoodListContract.Presenter
}