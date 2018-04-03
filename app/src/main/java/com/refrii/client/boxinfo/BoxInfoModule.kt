package com.refrii.client.boxinfo

import com.refrii.client.di.ActivityScoped
import dagger.Binds
import dagger.Module

@Module
abstract class BoxInfoModule {

    @ActivityScoped
    @Binds
    abstract fun providePresenter(presenter: BoxInfoPresenter): BoxInfoContract.Presenter
}