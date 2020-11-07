package app.muko.mypantry.noticelist

import app.muko.mypantry.di.scope.ActivityScoped
import dagger.Binds
import dagger.Module

@Module
abstract class NoticeListModule {

    @ActivityScoped
    @Binds
    abstract fun providePresenter(presenter: NoticeListPresenter): NoticeListContract.Presenter
}