package app.muko.mypantry.welcome

import app.muko.mypantry.di.ActivityScoped
import dagger.Binds
import dagger.Module

@Module
abstract class WelcomeModule {

    @ActivityScoped
    @Binds
    abstract fun providePresenter(presenter: WelcomePresenter): WelcomeContract.Presenter
}