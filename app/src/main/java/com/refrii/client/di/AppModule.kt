package com.refrii.client.di

import android.app.Application
import android.content.Context
import android.preference.PreferenceManager
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.refrii.client.BuildConfig
import com.refrii.client.boxinfo.BoxInfoContract
import com.refrii.client.boxinfo.BoxInfoPresenter
import com.refrii.client.data.api.source.ApiRepository
import com.refrii.client.data.google.GoogleRepository
import com.refrii.client.food.FoodContract
import com.refrii.client.food.FoodPresenter
import com.refrii.client.foodlist.FoodListContract
import com.refrii.client.foodlist.FoodListPresenter
import com.refrii.client.newfood.NewFoodContract
import com.refrii.client.newfood.NewFoodPresenter
import com.refrii.client.newunit.NewUnitContract
import com.refrii.client.newunit.NewUnitPresenter
import com.refrii.client.signin.SigninContract
import com.refrii.client.signin.SigninPresenter
import com.refrii.client.unit.UnitContract
import com.refrii.client.unit.UnitPresenter
import com.refrii.client.unitlist.UnitListContract
import com.refrii.client.unitlist.UnitListPresenter
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
class AppModule(private var mApplication: Application) {

    @Singleton
    @Provides
    fun provideApplicationContext(): Context {
        return mApplication.applicationContext
    }

    @Provides
    fun provideSigninPresenter(googleRepository: GoogleRepository, apiRepository: ApiRepository): SigninContract.Presenter {
        return SigninPresenter(googleRepository, apiRepository)
    }

    @Provides
    fun provideFoodListPresenter(apiRepository: ApiRepository): FoodListContract.Presenter {
        return FoodListPresenter(apiRepository)
    }

    fun provideFoodPresenter(apiRepository: ApiRepository): FoodContract.Presenter {
        return FoodPresenter(apiRepository)
    }

    fun provideNewFoodPresenter(apiRepository: ApiRepository): NewFoodContract.Presenter {
        return NewFoodPresenter(apiRepository)
    }

    fun provideBoxInfoPresenter(apiRepository: ApiRepository): BoxInfoContract.Presenter {
        return BoxInfoPresenter(apiRepository)
    }

    @Provides
    fun provideUnitListPresenter(apiRepository: ApiRepository): UnitListContract.Presenter {
        return UnitListPresenter(apiRepository)
    }

    @Provides
    fun provideUnitPresenter(apiRepository: ApiRepository): UnitContract.Presenter {
        return UnitPresenter(apiRepository)
    }

    @Provides
    fun provideNewUnitPresenter(apiRepository: ApiRepository): NewUnitContract.Presenter {
        return NewUnitPresenter(apiRepository)
    }

    @Singleton
    @Provides
    fun provideRetrofit(): Retrofit {
        val gson = GsonBuilder()
                .setLenient()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create()

        return Retrofit.Builder()
                .baseUrl(getApiEndpoint())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(provideHttpClient())
                .build()
    }

    @Singleton
    @Provides
    fun provideHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(provideApplicationContext())
                    val original = chain.request()
                    val jwt = sharedPreferences.getString("jwt", null)
                    val request = original.newBuilder()
                            .header("Accept", "application/json")

                    if (jwt != null) {
                        request.header("Authorization", "Bearer $jwt")
                    }

                    request.method(original.method(), original.body())
                    chain.proceed(request.build())
                }
                .build()
    }

    private fun getApiEndpoint(): String {
        val version = "v1"

        return when (BuildConfig.FLAVOR) {
            "debug" -> "https://refrii-api-staging.herokuapp.com/$version/"
            else -> "https://api.refrii.com/$version/"
        }
    }
}