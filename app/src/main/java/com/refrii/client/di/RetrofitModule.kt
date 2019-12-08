package com.refrii.client.di

import android.content.Context
import android.preference.PreferenceManager
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.refrii.client.BuildConfig
import com.refrii.client.R
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
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
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

        return OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val expiresAt = sharedPreferences.getLong(context.getString(R.string.preference_key_expiration_timestamp), 0) * 1000
                    val currentUser = FirebaseAuth.getInstance().currentUser
                    val original = chain.request()
                    val jwt = sharedPreferences.getString(context.getString(R.string.preference_key_jwt), null)
                    val request = original.newBuilder()
                            .header("Accept", "application/json")
                            .header("Authorization", "Bearer $jwt")

                    if (expiresAt < Date().time) {
                        val task = currentUser?.getIdToken(true)?.addOnCompleteListener {
                            val editor = sharedPreferences.edit()

                            editor.apply {
                                putString(context.getString(R.string.preference_key_jwt), it.result?.token)

                                it.result?.expirationTimestamp?.let {
                                    putLong(context.getString(R.string.preference_key_expiration_timestamp), it)
                                }
                            }
                            editor.apply()
                        }

                        if (task != null) {
                            Tasks.await(task).token?.let {
                                request.header("Authorization", "Bearer $it")
                            }
                        }
                    }

                    request.method(original.method(), original.body())
                    chain.proceed(request.build())
                }
                .build()
    }

    private fun getApiEndpoint(): String {
        val version = "v1"

        return if (BuildConfig.FLAVOR == "development") {
//            "http://192.168.1.104:3000/"
            "https://staging.api.refrii.com/$version/"
        } else {
            "https://api.refrii.com/$version/"
        }
    }
}
