package app.muko.mypantry.settings

import app.muko.mypantry.di.scope.ActivityScoped
import dagger.Binds
import dagger.Module

@Module
abstract class SettingsModule {

    @ActivityScoped
    @Binds
    abstract fun providePresenter(presenter: SettingsPresenter): SettingsContract.Presenter
}