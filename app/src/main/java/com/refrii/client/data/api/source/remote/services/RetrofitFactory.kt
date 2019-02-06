package com.refrii.client.data.api.source.remote.services

import android.content.Context
import android.preference.PreferenceManager

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.refrii.client.BuildConfig

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitFactory {

    private val TAG = "RetrofitFactory"

    fun <T> getClient(clazz: Class<T>, context: Context): T {
        val httpClient = OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
                    val original = chain.request()
                    val jwt = sharedPreferences.getString("jwt", null)
                    val request = original.newBuilder()
                            .header("Accept", "application/json")
                    if (jwt != null) {
                        request.header("Authorization", "Bearer " + jwt)
                    }
                    request.method(original.method(), original.body())
                    chain.proceed(request.build())
                }
                .build()
        val gson = GsonBuilder()
                .setLenient()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create()
        val retrofit = Retrofit.Builder()
                .baseUrl(getApiEndpoint())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(httpClient)
                .build()

        return retrofit.create(clazz)
    }

    fun getApiEndpoint(): String {
        val version = "v1"

        return when (BuildConfig.FLAVOR) {
            "debug" -> "https://refrii-api-staging.herokuapp.com/$version"
            else -> "https://api.refrii.com/$version"
        }
    }
}
