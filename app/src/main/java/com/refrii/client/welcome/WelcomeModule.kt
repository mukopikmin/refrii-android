package com.refrii.client.welcome

import com.refrii.client.di.ActivityScoped
import dagger.Binds
import dagger.Module

@Module
abstract class WelcomeModule {

    @ActivityScoped
    @Binds
    abstract fun providePresenter(presenter: WelcomePresenter): WelcomeContract.Presenter
}