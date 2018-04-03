package com.refrii.client.newfood

import com.refrii.client.di.ActivityScoped
import dagger.Binds
import dagger.Module

@Module
abstract class NewFoodModule {

    @ActivityScoped
    @Binds
    abstract fun providePresenter(presenter: NewFoodPresenter): NewFoodContract.Presenter
}