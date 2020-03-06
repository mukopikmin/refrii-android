package app.muko.mypantry.unitlist

import app.muko.mypantry.di.ActivityScoped
import dagger.Binds
import dagger.Module

@Module
abstract class UnitListModule {

    @ActivityScoped
    @Binds
    abstract fun providePresenter(presenter: UnitListPresenter): UnitListContract.Presenter
}