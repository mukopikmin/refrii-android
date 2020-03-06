package app.muko.mypantry.boxinfo

import app.muko.mypantry.di.ActivityScoped
import dagger.Binds
import dagger.Module

@Module
abstract class BoxInfoModule {

    @ActivityScoped
    @Binds
    abstract fun providePresenter(presenter: BoxInfoPresenter): BoxInfoContract.Presenter
}