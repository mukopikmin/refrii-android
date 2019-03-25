package com.refrii.client.newbox

import com.refrii.client.di.ActivityScoped
import dagger.Binds
import dagger.Module

@Module
abstract class NewBoxModule {

    @ActivityScoped
    @Binds
    abstract fun providePresenter(presenter: NewBoxPresenter): NewBoxContract.Presenter
}