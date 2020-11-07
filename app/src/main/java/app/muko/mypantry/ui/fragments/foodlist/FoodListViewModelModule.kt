package app.muko.mypantry.ui.fragments.foodlist

import androidx.lifecycle.ViewModel
import app.muko.mypantry.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class FoodListViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(FoodListViewModel::class)
    abstract fun bindFoodListViewModel(viewModel: FoodListViewModel): ViewModel
}