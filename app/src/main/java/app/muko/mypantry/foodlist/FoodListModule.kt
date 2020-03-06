package app.muko.mypantry.foodlist

import app.muko.mypantry.di.ActivityScoped
import dagger.Binds
import dagger.Module

@Module
abstract class FoodListModule {

    @ActivityScoped
    @Binds
    abstract fun providePresenter(presenter: FoodListPresenter): FoodListContract.Presenter
}