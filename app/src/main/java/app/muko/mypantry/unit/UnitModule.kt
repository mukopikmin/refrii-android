package app.muko.mypantry.unit

import app.muko.mypantry.di.ActivityScoped
import dagger.Binds
import dagger.Module

@Module
abstract class UnitModule {

    @ActivityScoped
    @Binds
    abstract fun providePresenter(presenter: UnitPresenter): UnitContract.Presenter
}