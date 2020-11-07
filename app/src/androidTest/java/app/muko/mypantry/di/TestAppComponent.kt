package app.muko.mypantry.di

import app.muko.mypantry.ui.activities.foodlist.FoodListActivityTest
import dagger.Component
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [
    TestAppModule::class,
    TestRetrofitModule::class,
    TestLocalDatabaseModule::class,
    ApiRepositoryModule::class,
    ActivityBindingModule::class,
    AndroidSupportInjectionModule::class])
interface TestAppComponent : AppComponent {
    fun inject(target: FoodListActivityTest)
}