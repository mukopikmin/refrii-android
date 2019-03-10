package com.refrii.client.data.api.source.remote

import com.refrii.client.data.api.models.Box
import com.refrii.client.data.api.models.Food
import com.refrii.client.data.api.models.Unit
import com.refrii.client.data.api.models.User
import com.refrii.client.data.api.source.ApiRepositoryCallback
import com.refrii.client.data.api.source.remote.services.BoxService
import com.refrii.client.data.api.source.remote.services.FoodService
import com.refrii.client.data.api.source.remote.services.UnitService
import com.refrii.client.data.api.source.remote.services.UserService
import okhttp3.MultipartBody
import retrofit2.Retrofit
import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*

class ApiRemoteDataSource(private val mRetrofit: Retrofit) {

    fun verify(): Observable<User> {
        return mRetrofit.create(UserService::class.java)
                .verify()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

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


    fun updateBox(box: Box, callback: ApiRepositoryCallback<Box>) {
        mRetrofit.create(BoxService::class.java)
                .updateBox(box.id, box.toMultipartBody())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getSubscriber(callback))
    }

    fun getFoodsInBox(id: Int): Observable<List<Food>> {
        return mRetrofit.create(BoxService::class.java)
                .getFoodsInBox(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun getFoods(): Observable<List<Food>> {
        return mRetrofit.create(FoodService::class.java)
                .getFodos()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun getFood(id: Int, callback: ApiRepositoryCallback<Food>) {
        mRetrofit.create(FoodService::class.java)
                .getFood(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getSubscriber(callback))
    }

    fun createFood(name: String, notice: String, amount: Double, box: Box, unit: Unit, expirationDate: Date, callback: ApiRepositoryCallback<Food>) {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val body = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("name", name)
                .addFormDataPart("notice", notice)
                .addFormDataPart("amount", amount.toString())
                .addFormDataPart("box_id", box.id.toString())
                .addFormDataPart("unit_id", unit.id.toString())
                .addFormDataPart("expiration_date", simpleDateFormat.format(expirationDate))
                .build()

        mRetrofit.create(FoodService::class.java)
                .addFood(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getSubscriber(callback))
    }

    fun updateFood(food: Food, box: Box, callback: ApiRepositoryCallback<Food>) {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val body = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("name", food.name)
                .addFormDataPart("notice", food.notice)
                .addFormDataPart("amount", food.amount.toString())
                .addFormDataPart("box_id", box.id.toString())
                .addFormDataPart("unit_id", food.unit?.id.toString())
                .addFormDataPart("expiration_date", simpleDateFormat.format(food.expirationDate))
                .build()

        mRetrofit.create(FoodService::class.java)
                .updateFood(food.id, body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getSubscriber(callback))
    }

    fun removeFood(id: Int, callback: ApiRepositoryCallback<Void>) {
        mRetrofit.create(FoodService::class.java)
                .removeFood(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getSubscriber(callback))
    }

    fun getUnits(): Observable<List<Unit>> {
        return mRetrofit.create(UnitService::class.java)
                .getUnits()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun getUnit(id: Int): Observable<Unit> {
        return mRetrofit.create(UnitService::class.java)
                .getUnit(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun createUnit(label: String, step: Double, callback: ApiRepositoryCallback<Unit>) {
        val body = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("label", label)
                .addFormDataPart("step", step.toString())
                .build()

        mRetrofit.create(UnitService::class.java)
                .createUnit(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getSubscriber(callback))
    }

    fun updateUnit(unit: Unit, callback: ApiRepositoryCallback<Unit>) {
        mRetrofit.create(UnitService::class.java)
                .updateUnit(unit.id, unit.toMultipartBody())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getSubscriber(callback))
    }

    fun removeUnit(id: Int, callback: ApiRepositoryCallback<Void>) {
        mRetrofit.create(UnitService::class.java)
                .deleteUnit(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getSubscriber(callback))
    }

    private fun <T> getSubscriber(callback: ApiRepositoryCallback<T>): Subscriber<T> {
        return object : Subscriber<T>() {
            override fun onNext(t: T) {
                callback.onNext(t)
            }

            override fun onCompleted() {
                callback.onCompleted()
            }

            override fun onError(e: Throwable?) {
                callback.onError(e)
            }
        }
    }
}