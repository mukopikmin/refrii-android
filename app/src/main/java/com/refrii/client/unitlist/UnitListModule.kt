package com.refrii.client.unitlist

import com.refrii.client.di.ActivityScoped
import dagger.Binds
import dagger.Module

@Module
abstract class UnitListModule {

    @ActivityScoped
    @Binds
    abstract fun providePresenter(presenter: UnitListPresenter): UnitListContract.Presenter
}