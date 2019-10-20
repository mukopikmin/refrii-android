package com.refrii.client.noticelist

import com.refrii.client.di.ActivityScoped
import dagger.Binds
import dagger.Module

@Module
abstract class NoticeListModule {

    @ActivityScoped
    @Binds
    abstract fun providePresenter(presenter: NoticeListPresenter): NoticeListContract.Presenter
}