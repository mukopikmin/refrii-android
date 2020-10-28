package app.muko.mypantry.di

import app.muko.mypantry.App
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [
    AppModule::class,
    RetrofitModule::class,
    LocalDatabaseModule::class,
    ApiRepositoryModule::class,
    ActivityBindingModule::class,
    AndroidInjectionModule::class
])
interface AppComponent : AndroidInjector<App> {

    @Component.Builder
    interface Builder {
        fun appModule(appModule: AppModule): Builder
        fun build(): AppComponent
    }
}