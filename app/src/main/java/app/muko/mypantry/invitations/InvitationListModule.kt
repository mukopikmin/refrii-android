package app.muko.mypantry.invitations

import app.muko.mypantry.boxinfo.BoxInfoContract
import app.muko.mypantry.boxinfo.BoxInfoPresenter
import app.muko.mypantry.di.scope.ActivityScoped
import dagger.Binds
import dagger.Module

@Module
abstract class InvitationListModule {

    @ActivityScoped
    @Binds
    abstract fun providePresenter(presenter: BoxInfoPresenter): BoxInfoContract.Presenter
}