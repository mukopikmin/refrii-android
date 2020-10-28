package app.muko.mypantry.fragments.navigation

import androidx.lifecycle.ViewModel
import app.muko.mypantry.di.ViewModelKey
import app.muko.mypantry.fragments.foodlist.FoodListViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class FoodActionDialogViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(FoodActionDialogViewModel::class)
    abstract fun bindFoodActionDialogViewModel(viewModel: FoodActionDialogViewModel): ViewModel
}