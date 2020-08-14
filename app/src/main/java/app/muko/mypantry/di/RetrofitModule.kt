package app.muko.mypantry.di

import android.content.Context
import app.muko.mypantry.BuildConfig
import app.muko.mypantry.data.source.remote.services.*
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
class RetrofitModule {

    @Singleton
    @Provides
    fun provideRetrofit(context: Context): Retrofit {
        val gson = GsonBuilder()
                .setLenient()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create()

        return Retrofit.Builder()
                .baseUrl(getApiEndpoint())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(provideHttpClient(context))
                .build()
    }

    @Singleton
    @Provides
    fun provideBoxService(retrofit: Retrofit): BoxService {
        return retrofit.create(BoxService::class.java)
    }

    @Singleton
    @Provides
    fun provideFoodService(retrofit: Retrofit): FoodService {
        return retrofit.create(FoodService::class.java)
    }

    @Singleton
    @Provides
    fun provideUnitService(retrofit: Retrofit): UnitService {
        return retrofit.create(UnitService::class.java)
    }

    @Singleton
    @Provides
    fun provideShopPlanService(retrofit: Retrofit): ShopPlanService {
        return retrofit.create(ShopPlanService::class.java)
    }

    @Singleton
    @Provides
    fun provideNoticeService(retrofit: Retrofit): NoticeService {
        return retrofit.create(NoticeService::class.java)
    }

    @Singleton
    @Provides
    fun provideHttpClient(context: Context): OkHttpClient {
        return OkHttpClient.Builder()
                .addInterceptor(AuthorizationInterceptor(context))
                .addInterceptor(ApiErrorInterceptor(context))
                .build()
    }

    private fun getApiEndpoint(): String {
        val version = "v1"

        return if (BuildConfig.FLAVOR == "development") {
            "https://staging.api.mypantry.muko.app/$version/"
            "http://192.168.1.102:3000/"
        } else {
            "https://api.mypantry.muko.app/$version/"
        }
    }
}
