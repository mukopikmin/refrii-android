package com.refrii.client.invitations

import com.refrii.client.boxinfo.BoxInfoContract
import com.refrii.client.boxinfo.BoxInfoPresenter
import com.refrii.client.di.ActivityScoped
import dagger.Binds
import dagger.Module

@Module
abstract class InvitationListModule {

    @ActivityScoped
    @Binds
    abstract fun providePresenter(presenter: BoxInfoPresenter): BoxInfoContract.Presenter
}