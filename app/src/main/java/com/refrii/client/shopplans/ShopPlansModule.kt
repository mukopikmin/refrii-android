package com.refrii.client.shopplans

import com.refrii.client.di.ActivityScoped
import dagger.Binds
import dagger.Module

@Module
abstract class ShopPlansModule {

    @ActivityScoped
    @Binds
    abstract fun providePresenter(presenter: ShopPlansPresenter): ShopPlansContract.Presenter
}