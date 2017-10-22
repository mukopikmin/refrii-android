package com.refrii.client

import android.content.Context
import android.content.SharedPreferences
import android.os.AsyncTask
import android.preference.PreferenceManager
import android.util.Log

import com.google.android.gms.auth.GoogleAuthException
import com.google.android.gms.auth.GoogleAuthUtil
import com.google.android.gms.auth.UserRecoverableAuthException
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder

import java.io.IOException
import java.util.Date
import java.util.HashMap

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitFactory {

    private val TAG = "RetrofitFactory"

    fun <T> getClient(clazz: Class<T>, context: Context): T {
        val httpClient = OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)//context.getSharedPreferences("DATA", Context.MODE_PRIVATE);
                    val original = chain.request()
                    val jwt = sharedPreferences.getString("jwt", null)
                    val request = original.newBuilder()
                            .header("Accept", "application/json")
                            .header("Authorization", "Bearer " + jwt!!)
                            .method(original.method(), original.body())
                            .build()

                    chain.proceed(request)
                }
                .build()
        val gson = GsonBuilder()
                .setLenient()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create()
        val retrofit = Retrofit.Builder()
                .baseUrl("https://refrii-api.herokuapp.com/")
                //       .baseUrl("http://192.168.10.102:3000/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(httpClient)
                .build()
        return retrofit.create(clazz)
    }
}
