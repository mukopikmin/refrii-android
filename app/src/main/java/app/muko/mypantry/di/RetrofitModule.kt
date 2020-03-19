package app.muko.mypantry.di

import android.content.Context
import app.muko.mypantry.BuildConfig
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
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
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(provideHttpClient(context))
                .build()
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
        } else {
            "https://api.mypantry.muko.app/$version/"
        }
    }
}
