package app.muko.mypantry.newunit

import app.muko.mypantry.di.ActivityScoped
import dagger.Binds
import dagger.Module

@Module
abstract class NewUnitModule {

    @ActivityScoped
    @Binds
    abstract fun providePresenter(presenter: NewUnitPresenter): NewUnitContract.Presenter
}