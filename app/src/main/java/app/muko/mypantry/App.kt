package app.muko.mypantry

import android.app.Application
import app.muko.mypantry.di.AppComponent
import app.muko.mypantry.di.AppModule
import app.muko.mypantry.di.DaggerAppComponent

class App : Application() {

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAppComponent.builder()
                .appModule(AppModule(this))
                .build()
    }

    fun getComponent(): AppComponent {
        return appComponent
    }
}
