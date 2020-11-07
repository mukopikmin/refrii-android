package app.muko.mypantry.ui.fragments.expiring

import androidx.lifecycle.ViewModel
import app.muko.mypantry.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ExpiringFoodsViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(ExpiringFoodsViewModel::class)
    abstract fun bindExpiringFoodsViewModel(viewModel: ExpiringFoodsViewModel): ViewModel
}