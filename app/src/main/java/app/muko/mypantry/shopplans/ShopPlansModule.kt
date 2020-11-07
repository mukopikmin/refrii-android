package app.muko.mypantry.shopplans

import app.muko.mypantry.di.scope.ActivityScoped
import dagger.Binds
import dagger.Module

@Module
abstract class ShopPlansModule {

    @ActivityScoped
    @Binds
    abstract fun providePresenter(presenter: ShopPlansPresenter): ShopPlansContract.Presenter
}