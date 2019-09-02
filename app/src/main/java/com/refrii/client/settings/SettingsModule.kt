package com.refrii.client.settings

import com.refrii.client.di.ActivityScoped
import dagger.Binds
import dagger.Module

@Module
abstract class SettingsModule {

    @ActivityScoped
    @Binds
    abstract fun providePresenter(presenter: SettingsPresenter): SettingsContract.Presenter
}