package com.refrii.client.data.source.remote

import com.refrii.client.data.models.Box
import com.refrii.client.data.models.Food
import com.refrii.client.data.models.ShopPlan
import com.refrii.client.data.models.Unit
import com.refrii.client.data.source.remote.services.FoodService
import okhttp3.MultipartBody
import retrofit2.Retrofit
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*

class ApiRemoteFoodSource(private val mRetrofit: Retrofit) {

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

    fun getShopPlansForFood(id: Int): Observable<List<ShopPlan>> {
        return mRetrofit.create(FoodService::class.java)
                .getShopPlans(id)
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

    fun updateFood(id: Int, name: String?, notice: String?, amount: Double?, expirationDate: Date?, boxId: Int?, unitId: Int?): Observable<Food> {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val bodyBuilder = MultipartBody.Builder()
                .setType(MultipartBody.FORM)

        name?.let { bodyBuilder.addFormDataPart("name", it) }
        notice?.let { bodyBuilder.addFormDataPart("notice", it) }
        amount?.let { bodyBuilder.addFormDataPart("amount", it.toString()) }
        expirationDate?.let { bodyBuilder.addFormDataPart("expiration_date", simpleDateFormat.format(it)) }
        boxId?.let { bodyBuilder.addFormDataPart("box_id", it.toString()) }
        unitId?.let { bodyBuilder.addFormDataPart("unit_id", it.toString()) }

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
}