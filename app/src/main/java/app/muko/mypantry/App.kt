package app.muko.mypantry

import android.app.Activity
import android.app.Application
import app.muko.mypantry.di.AppComponent
import app.muko.mypantry.di.AppModule
import app.muko.mypantry.di.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import javax.inject.Inject

class App : Application(), HasAndroidInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Any>

    override fun androidInjector(): AndroidInjector<Any> {
        DaggerAppComponent.builder()
                .appModule(AppModule(this))
                .build()
                .inject(this)

        return dispatchingAndroidInjector
    }
}
