package app.muko.mypantry.signin

import app.muko.mypantry.di.ActivityScoped
import dagger.Binds
import dagger.Module

@Module
abstract class SigninModule {

    @ActivityScoped
    @Binds
    abstract fun providePresenter(presenter: SigninPresenter): SigninContract.Presenter
}