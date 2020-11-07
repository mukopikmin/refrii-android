package app.muko.mypantry.newfood

import app.muko.mypantry.di.scope.ActivityScoped
import dagger.Binds
import dagger.Module

@Module
abstract class NewFoodModule {

    @ActivityScoped
    @Binds
    abstract fun providePresenter(presenter: NewFoodPresenter): NewFoodContract.Presenter
}