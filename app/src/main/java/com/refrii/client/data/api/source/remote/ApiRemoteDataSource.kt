package com.refrii.client.data.api.source.remote

import com.refrii.client.data.api.models.Box
import com.refrii.client.data.api.models.Credential
import com.refrii.client.data.api.models.Food
import com.refrii.client.data.api.models.Unit
import com.refrii.client.data.api.source.ApiDataSource
import com.refrii.client.data.api.source.ApiRepositoryCallback
import com.refrii.client.data.api.source.remote.services.AuthService
import com.refrii.client.data.api.source.remote.services.BoxService
import com.refrii.client.data.api.source.remote.services.FoodService
import com.refrii.client.data.api.source.remote.services.UnitService
import okhttp3.MultipartBody
import retrofit2.Retrofit
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*

class ApiRemoteDataSource(private val mRetrofit: Retrofit) : ApiDataSource {

    override fun auth(googleToken: String, callback: ApiRepositoryCallback<Credential>) {
        val params = HashMap<String, String>()
        params["token"] = googleToken

        mRetrofit.create(AuthService::class.java)
                .getToken(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Subscriber<Credential>() {
                    override fun onNext(t: Credential) {
                        callback.onNext(t)
                    }

                    override fun onCompleted() {
                        callback.onCompleted()
                    }

                    override fun onError(e: Throwable) {
                        callback.onError(e)
                    }
                })
    }

    override fun getBoxes(callback: ApiRepositoryCallback<List<Box>>) {
        mRetrofit.create(BoxService::class.java)
                .getBoxes()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Subscriber<List<Box>>() {
                    override fun onNext(t: List<Box>?) {
                        callback.onNext(t)
                    }

                    override fun onCompleted() {
                        callback.onCompleted()
                    }

                    override fun onError(e: Throwable?) {
                        callback.onError(e)
                    }
                })
    }

    override fun getBox(id: Int, callback: ApiRepositoryCallback<Box>) {
        mRetrofit.create(BoxService::class.java)
                .getBox(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Subscriber<Box>() {
                    override fun onNext(t: Box?) {
                        callback.onNext(t)
                    }

                    override fun onCompleted() {
                        callback.onCompleted()
                    }

                    override fun onError(e: Throwable?) {
                        callback.onError(e)
                    }
                })
    }


    override fun updateBox(box: Box, callback: ApiRepositoryCallback<Box>) {
        mRetrofit.create(BoxService::class.java)
                .updateBox(box.id, box.toMultipartBody())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Subscriber<Box>() {
                    override fun onNext(t: Box?) {
                        callback.onNext(t)
                    }

                    override fun onCompleted() {
                        callback.onCompleted()
                    }

                    override fun onError(e: Throwable?) {
                        callback.onError(e)
                    }
                })
    }

    override fun getFoodsInBox(id: Int, callback: ApiRepositoryCallback<List<Food>>) {
        mRetrofit.create(BoxService::class.java)
                .getFoodsInBox(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Subscriber<List<Food>>() {
                    override fun onNext(t: List<Food>?) {
                        callback.onNext(t)
                    }

                    override fun onCompleted() {
                        callback.onCompleted()
                    }

                    override fun onError(e: Throwable?) {
                        callback.onError(e)
                    }

                })
    }

    override fun getFood(id: Int, callback: ApiRepositoryCallback<Food>) {
        mRetrofit.create(FoodService::class.java)
                .getFood(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Subscriber<Food>() {
                    override fun onNext(t: Food?) {
                        callback.onNext(t)
                    }

                    override fun onCompleted() {
                        callback.onCompleted()
                    }

                    override fun onError(e: Throwable?) {
                        callback.onError(e)
                    }
                })
    }

    override fun createFood(name: String, notice: String, amount: Double, box: Box, unit: Unit, expirationDate: Date, callback: ApiRepositoryCallback<Food>) {
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
                .subscribe(object : Subscriber<Food>() {
                    override fun onCompleted() {
                        callback.onCompleted()
                    }

                    override fun onNext(t: Food?) {
                        callback.onNext(t)
                    }

                    override fun onError(e: Throwable?) {
                        callback.onError(e)
                    }
                })
    }

    override fun updateFood(food: Food, box: Box, callback: ApiRepositoryCallback<Food>) {
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
                .subscribe(object : Subscriber<Food>() {
                    override fun onNext(t: Food?) {
                        callback.onNext(t)
                    }

                    override fun onCompleted() {
                        callback.onCompleted()
                    }

                    override fun onError(e: Throwable?) {
                        callback.onError(e)
                    }
                })
    }

    override fun removeFood(id: Int, callback: ApiRepositoryCallback<Void>) {
        mRetrofit.create(FoodService::class.java)
                .removeFood(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Subscriber<Void>() {
                    override fun onNext(t: Void?) {
                        callback.onNext(t)
                    }

                    override fun onCompleted() {
                        callback.onCompleted()
                    }

                    override fun onError(e: Throwable?) {
                        callback.onError(e)
                    }
                })
    }

    override fun getUnits(userId: Int, callback: ApiRepositoryCallback<List<Unit>>) {
        mRetrofit.create(UnitService::class.java)
                .getUnits()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Subscriber<MutableList<Unit>>() {
                    override fun onNext(t: MutableList<Unit>?) {
                        callback.onNext(t)
                    }

                    override fun onCompleted() {
                        callback.onCompleted()
                    }

                    override fun onError(e: Throwable?) {
                        callback.onError(e)
                    }
                })
    }

    override fun getUnit(id: Int, callback: ApiRepositoryCallback<Unit>) {
        mRetrofit.create(UnitService::class.java)
                .getUnit(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Subscriber<Unit>() {
                    override fun onNext(t: Unit?) {
                        callback.onNext(t)
                    }

                    override fun onCompleted() {
                        callback.onCompleted()
                    }

                    override fun onError(e: Throwable?) {
                        callback.onCompleted()
                    }
                })
    }

    override fun createUnit(label: String, step: Double, callback: ApiRepositoryCallback<Unit>) {
        val body = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("label", label)
                .addFormDataPart("step", step.toString())
                .build()

        mRetrofit.create(UnitService::class.java)
                .createUnit(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Subscriber<Unit>() {
                    override fun onCompleted() {
                        callback.onCompleted()
                    }

                    override fun onNext(t: Unit) {
                        callback.onNext(t)
                    }

                    override fun onError(e: Throwable) {
                        callback.onError(e)
                    }

                })
    }

    override fun updateUnit(unit: Unit, callback: ApiRepositoryCallback<Unit>) {
        mRetrofit.create(UnitService::class.java)
                .updateUnit(unit.id, unit.toMultipartBody())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Subscriber<Unit>() {
                    override fun onCompleted() {
                        callback.onCompleted()
                    }

                    override fun onNext(t: Unit?) {
                        callback.onNext(t)
                    }

                    override fun onError(e: Throwable?) {
                        callback.onError(e)
                    }
                })
    }

    override fun removeUnit(id: Int, callback: ApiRepositoryCallback<Void>) {
        mRetrofit.create(UnitService::class.java)
                .deleteUnit(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Subscriber<Void>() {
                    override fun onNext(t: Void?) {
                        callback.onNext(t)
                    }

                    override fun onCompleted() {
                        callback.onCompleted()
                    }

                    override fun onError(e: Throwable?) {
                        callback.onError(e)
                    }
                })
    }
}