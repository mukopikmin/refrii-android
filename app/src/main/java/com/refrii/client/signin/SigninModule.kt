package com.refrii.client.signin

import com.refrii.client.di.ActivityScoped
import dagger.Binds
import dagger.Module

@Module
abstract class SigninModule {

    @ActivityScoped
    @Binds
    abstract fun providePresenter(presenter: SigninPresenter): SigninContract.Presenter
}