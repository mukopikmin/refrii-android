package com.refrii.client.unit

import com.refrii.client.di.ActivityScoped
import dagger.Binds
import dagger.Module

@Module
abstract class UnitModule {

    @ActivityScoped
    @Binds
    abstract fun providePresenter(presenter: UnitPresenter): UnitContract.Presenter
}