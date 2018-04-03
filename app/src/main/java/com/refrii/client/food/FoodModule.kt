package com.refrii.client.food

import com.refrii.client.di.ActivityScoped
import dagger.Binds
import dagger.Module

@Module
abstract class FoodModule {

    @ActivityScoped
    @Binds
    abstract fun providePresenter(presenter: FoodPresenter): FoodContract.Presenter
}