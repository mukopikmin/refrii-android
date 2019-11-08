package com.refrii.client

import android.app.Application
import com.refrii.client.di.AppComponent
import com.refrii.client.di.AppModule
import com.refrii.client.di.DaggerAppComponent

class App : Application() {

    private lateinit var appComponent: AppComponent

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
