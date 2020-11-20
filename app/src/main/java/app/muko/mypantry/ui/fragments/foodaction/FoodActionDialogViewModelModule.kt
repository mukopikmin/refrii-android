package app.muko.mypantry.ui.fragments.foodaction

import androidx.lifecycle.ViewModel
import app.muko.mypantry.di.ViewModelKey
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