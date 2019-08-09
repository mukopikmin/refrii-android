package com.refrii.client.di

import android.app.Application
import android.content.Context
import android.preference.PreferenceManager
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.refrii.client.BuildConfig
import com.refrii.client.R
import com.refrii.client.boxinfo.BoxInfoContract
import com.refrii.client.boxinfo.BoxInfoPresenter
import com.refrii.client.data.source.*
import com.refrii.client.food.FoodContract
import com.refrii.client.food.FoodPresenter
import com.refrii.client.foodlist.FoodListContract
import com.refrii.client.foodlist.FoodListPresenter
import com.refrii.client.newfood.NewFoodContract
import com.refrii.client.newfood.NewFoodPresenter
import com.refrii.client.newunit.NewUnitContract
import com.refrii.client.newunit.NewUnitPresenter
import com.refrii.client.shopplans.ShopPlansContract
import com.refrii.client.shopplans.ShopPlansPresenter
import com.refrii.client.signin.SigninContract
import com.refrii.client.signin.SigninPresenter
import com.refrii.client.unit.UnitContract
import com.refrii.client.unit.UnitPresenter
import com.refrii.client.unitlist.UnitListContract
import com.refrii.client.unitlist.UnitListPresenter
import com.refrii.client.welcome.WelcomeContract
import com.refrii.client.welcome.WelcomePresenter
import dagger.Module
import dagger.Provides
import io.realm.Realm
import io.realm.RealmConfiguration
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
    fun provideSigninPresenter(apiUserRepository: ApiUserRepository): SigninContract.Presenter {
        return SigninPresenter(apiUserRepository)
    }

    @Provides
    fun provideFoodListPresenter(boxRepository: ApiBoxRepository, foodRepository: ApiFoodRepository, userRepository: ApiUserRepository): FoodListContract.Presenter {
        return FoodListPresenter(boxRepository, foodRepository, userRepository)
    }

    @Provides
    fun provideFoodPresenter(foodRepository: ApiFoodRepository, boxRepository: ApiBoxRepository, shopPlanRepository: ApiShopPlanRepository): FoodContract.Presenter {
        return FoodPresenter(foodRepository, boxRepository, shopPlanRepository)
    }

    @Provides
    fun provideNewFoodPresenter(boxRepository: ApiBoxRepository, foodRepository: ApiFoodRepository): NewFoodContract.Presenter {
        return NewFoodPresenter(boxRepository, foodRepository)
    }

    @Provides
    fun provideBoxInfoPresenter(boxRepository: ApiBoxRepository): BoxInfoContract.Presenter {
        return BoxInfoPresenter(boxRepository)
    }

    @Provides
    fun provideUnitListPresenter(unitRepository: ApiUnitRepository): UnitListContract.Presenter {
        return UnitListPresenter(unitRepository)
    }

    @Provides
    fun provideUnitPresenter(unitRepository: ApiUnitRepository): UnitContract.Presenter {
        return UnitPresenter(unitRepository)
    }

    @Provides
    fun provideNewUnitPresenter(unitRepository: ApiUnitRepository): NewUnitContract.Presenter {
        return NewUnitPresenter(unitRepository)
    }

    @Provides
    fun provideShopPlansPresenter(shopPlanRepository: ApiShopPlanRepository): ShopPlansContract.Presenter {
        return ShopPlansPresenter(shopPlanRepository)
    }

    @Provides
    fun provideWelcomePresenter(boxRepository: ApiBoxRepository): WelcomeContract.Presenter {
        return WelcomePresenter(boxRepository)
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
    fun provideRealm(): Realm {
        Realm.init(provideApplicationContext())
        Realm.setDefaultConfiguration(RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build())

        return Realm.getDefaultInstance()
    }

    @Singleton
    @Provides
    fun provideHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(provideApplicationContext())
                    val original = chain.request()
                    val jwt = sharedPreferences.getString(provideApplicationContext().getString(R.string.preference_key_jwt), null)
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

        return if (BuildConfig.FLAVOR == "staging") {
//            "http://192.168.1.104:3000/"
            "https://staging.api.refrii.com/$version/"
        } else {
            "https://api.refrii.com/$version/"
        }
    }
}
