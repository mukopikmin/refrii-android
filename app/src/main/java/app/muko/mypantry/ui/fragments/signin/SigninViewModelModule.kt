package app.muko.mypantry.ui.fragments.signin

import androidx.lifecycle.ViewModel
import app.muko.mypantry.di.ViewModelKey
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