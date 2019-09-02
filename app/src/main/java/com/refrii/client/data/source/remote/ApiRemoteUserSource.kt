package com.refrii.client.data.source.remote

import com.refrii.client.data.models.User
import com.refrii.client.data.source.remote.services.UserService
import okhttp3.MultipartBody
import retrofit2.Retrofit
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class ApiRemoteUserSource(private val mRetrofit: Retrofit) {

    fun signup(): Observable<User> {
        return mRetrofit.create(UserService::class.java)
                .signupWithGoogle()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun verify(): Observable<User> {
        return mRetrofit.create(UserService::class.java)
                .verify()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun registerPushToken(id: Int, token: String?): Observable<User> {
        val builder = MultipartBody.Builder()
                .setType(MultipartBody.FORM)

        token?.let { builder.addFormDataPart("token", it) }

        return mRetrofit.create(UserService::class.java)
                .registerPushToken(id, builder.build())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun update(id: Int, name: String?): Observable<User> {
        val builder = MultipartBody.Builder()
                .setType(MultipartBody.FORM)

        name?.let { builder.addFormDataPart("name", it) }

        return mRetrofit.create(UserService::class.java)
                .update(id, builder.build())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }
}