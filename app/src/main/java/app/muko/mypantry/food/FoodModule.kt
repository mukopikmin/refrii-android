package app.muko.mypantry.food

import app.muko.mypantry.di.ActivityScoped
import dagger.Binds
import dagger.Module

@Module
abstract class FoodModule {

    @ActivityScoped
    @Binds
    abstract fun providePresenter(presenter: FoodPresenter): FoodContract.Presenter
}