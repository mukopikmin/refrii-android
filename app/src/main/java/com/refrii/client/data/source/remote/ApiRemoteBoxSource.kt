package com.refrii.client.data.source.remote

import com.refrii.client.data.models.Box
import com.refrii.client.data.models.Food
import com.refrii.client.data.models.Invitation
import com.refrii.client.data.models.Unit
import com.refrii.client.data.source.remote.services.BoxService
import okhttp3.MultipartBody
import retrofit2.Retrofit
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class ApiRemoteBoxSource(private val mRetrofit: Retrofit) {

    fun getBoxes(): Observable<List<Box>> {
        return mRetrofit.create(BoxService::class.java)
                .getBoxes()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun getBox(id: Int): Observable<Box> {
        return mRetrofit.create(BoxService::class.java)
                .getBox(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun createBox(name: String, notice: String?): Observable<Box> {
        val builder = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("name", name)

        notice?.let { builder.addFormDataPart("notice", notice) }

        return mRetrofit.create(BoxService::class.java)
                .createBox(builder.build())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }


    fun updateBox(id: Int, name: String?, notice: String?): Observable<Box> {
        val builder = MultipartBody.Builder()
                .setType(MultipartBody.FORM)

        name?.let { builder.addFormDataPart("name", it) }
        notice?.let { builder.addFormDataPart("notice", it) }

        return mRetrofit.create(BoxService::class.java)
                .updateBox(id, builder.build())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun removeBox(id: Int): Observable<Void> {
        return mRetrofit.create(BoxService::class.java)
                .removeBox(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun getFoodsInBox(id: Int): Observable<List<Food>> {
        return mRetrofit.create(BoxService::class.java)
                .getFoodsInBox(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun getUnitsForBox(id: Int): Observable<List<Unit>> {
        return mRetrofit.create(BoxService::class.java)
                .getUnitsForBox(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun invite(boxId: Int, email: String): Observable<Invitation> {
        val body = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("email", email)
                .build()

        return mRetrofit.create(BoxService::class.java)
                .invite(boxId, body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }
}