package com.refrii.client.newunit

import com.refrii.client.di.ActivityScoped
import dagger.Binds
import dagger.Module

@Module
abstract class NewUnitModule {

    @ActivityScoped
    @Binds
    abstract fun providePresenter(presenter: NewUnitPresenter): NewUnitContract.Presenter
}