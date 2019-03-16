package com.refrii.client.data.api.source.remote

import com.refrii.client.data.api.models.Box
import com.refrii.client.data.api.models.Food
import com.refrii.client.data.api.models.Unit
import com.refrii.client.data.api.models.User
import com.refrii.client.data.api.source.remote.services.BoxService
import com.refrii.client.data.api.source.remote.services.FoodService
import com.refrii.client.data.api.source.remote.services.UnitService
import com.refrii.client.data.api.source.remote.services.UserService
import okhttp3.MultipartBody
import retrofit2.Retrofit
import rx.Observable
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


    fun updateBox(box: Box): Observable<Box> {
        return mRetrofit.create(BoxService::class.java)
                .updateBox(box.id, box.toMultipartBody())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
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

    fun getFood(id: Int): Observable<Food> {
        return mRetrofit.create(FoodService::class.java)
                .getFood(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun createFood(name: String, notice: String, amount: Double, box: Box, unit: Unit, expirationDate: Date): Observable<Food> {
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

        return mRetrofit.create(FoodService::class.java)
                .addFood(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun updateFood(food: Food, box: Box): Observable<Food> {
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

        return mRetrofit.create(FoodService::class.java)
                .updateFood(food.id, body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun updateFood(id: Int, name: String? = null, notice: String? = null, amount: Double? = null, expirationDate: Date? = null): Observable<Food> {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val bodyBuilder = MultipartBody.Builder()
                .setType(MultipartBody.FORM)

        name?.let { bodyBuilder.addFormDataPart("name", it) }
        notice?.let { bodyBuilder.addFormDataPart("notice", it) }
        amount?.let { bodyBuilder.addFormDataPart("amount", it.toString()) }
        expirationDate?.let { bodyBuilder.addFormDataPart("expiration_date", simpleDateFormat.format(it)) }

        return mRetrofit.create(FoodService::class.java)
                .updateFood(id, bodyBuilder.build())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun removeFood(id: Int): Observable<Void> {
        return mRetrofit.create(FoodService::class.java)
                .removeFood(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
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

    fun createUnit(label: String, step: Double): Observable<Unit> {
        val body = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("label", label)
                .addFormDataPart("step", step.toString())
                .build()

        return mRetrofit.create(UnitService::class.java)
                .createUnit(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun updateUnit(unit: Unit): Observable<Unit> {
        return mRetrofit.create(UnitService::class.java)
                .updateUnit(unit.id, unit.toMultipartBody())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun removeUnit(id: Int): Observable<Void> {
        return mRetrofit.create(UnitService::class.java)
                .deleteUnit(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }
}