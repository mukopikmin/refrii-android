package app.muko.mypantry.shopplans

import app.muko.mypantry.di.ActivityScoped
import dagger.Binds
import dagger.Module

@Module
abstract class ShopPlansModule {

    @ActivityScoped
    @Binds
    abstract fun providePresenter(presenter: ShopPlansPresenter): ShopPlansContract.Presenter
}