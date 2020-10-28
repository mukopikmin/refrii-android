package app.muko.mypantry.fragments.signin

import androidx.lifecycle.ViewModel
import app.muko.mypantry.di.ViewModelKey
import app.muko.mypantry.fragments.foodlist.FoodListViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class SigninViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(SigninViewModel::class)
    abstract fun bindFoodActionDialogViewModel(viewModel: SigninViewModel): ViewModel
}