package app.muko.mypantry.data.source.remote

import app.muko.mypantry.data.models.User
import app.muko.mypantry.data.source.remote.services.UserService
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.MultipartBody
import retrofit2.Retrofit

class ApiRemoteUserSource(private val mRetrofit: Retrofit) {

    fun signup(): Flowable<User> {
        return mRetrofit.create(UserService::class.java)
                .signupWithGoogle()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun verify(): Flowable<User> {
        return mRetrofit.create(UserService::class.java)
                .verify()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun registerPushToken(id: Int, token: String?): Flowable<User> {
        val builder = MultipartBody.Builder()
                .setType(MultipartBody.FORM)

        token?.let { builder.addFormDataPart("token", it) }

        return mRetrofit.create(UserService::class.java)
                .registerPushToken(id, builder.build())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun update(id: Int, name: String?): Flowable<User> {
        val builder = MultipartBody.Builder()
                .setType(MultipartBody.FORM)

        name?.let { builder.addFormDataPart("name", it) }

        return mRetrofit.create(UserService::class.java)
                .update(id, builder.build())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }
}