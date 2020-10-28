package app.muko.mypantry.foodlist

import android.app.Activity
import androidx.lifecycle.ViewModel
import app.muko.mypantry.di.ActivityScoped
import app.muko.mypantry.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.multibindings.IntoMap
import dagger.android.ContributesAndroidInjector

@Module
abstract class FoodListModule {

//    @ActivityScoped
//    @Binds
//    abstract fun providePresenter(presenter: FoodListPresenter): FoodListContract.Presenter

    @Binds
    @IntoMap
    @ViewModelKey(FoodListViewModel::class)
    abstract fun bindFoodListViewModel(viewModel: FoodListViewModel): ViewModel
}