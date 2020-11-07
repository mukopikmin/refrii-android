package app.muko.mypantry.ui.activities.foodlist

import androidx.lifecycle.ViewModel
import app.muko.mypantry.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

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