package com.refrii.client.data.api.source

import com.refrii.client.data.api.models.Box
import com.refrii.client.data.api.models.Credential
import com.refrii.client.data.api.models.Food
import com.refrii.client.data.api.models.Unit
import com.refrii.client.data.api.source.local.ApiLocalDataSource
import com.refrii.client.data.api.source.remote.ApiRemoteDataSource
import io.realm.Realm
import retrofit2.Retrofit
import rx.Subscriber
import java.util.*

class ApiRepository(realm: Realm, retrofit: Retrofit) {

    private val mApiRemoteDataSource = ApiRemoteDataSource(retrofit)
    private val mApiLocalDataSource = ApiLocalDataSource(realm)

    fun auth(googleToken: String, callback: ApiRepositoryCallback<Credential>) {
        mApiRemoteDataSource.auth(googleToken, callback)
    }

    fun getBoxes(callback: ApiRepositoryCallback<List<Box>>): List<Box> {
        mApiRemoteDataSource.getBoxes(callback)
                .subscribe(object : Subscriber<List<Box>>() {
                    override fun onNext(t: List<Box>) {
                        mApiLocalDataSource.saveBoxes(t)
                        callback.onNext(t)
                    }

                    override fun onCompleted() {
                        callback.onCompleted()
                    }

                    override fun onError(e: Throwable) {
                        callback.onError(e)
                    }
                })

        return mApiLocalDataSource.getBoxes()
    }

    fun getBox(id: Int, callback: ApiRepositoryCallback<Box>) {
        mApiRemoteDataSource.getBox(id, callback)
    }

    fun getFoodsInBox(id: Int, callback: ApiRepositoryCallback<List<Food>>): List<Food> {
        mApiRemoteDataSource.getFoodsInBox(id, callback)
                .subscribe(object : Subscriber<List<Food>>() {
                    override fun onNext(t: List<Food>) {
                        mApiLocalDataSource.saveFoods(t, id)
                        callback.onNext(t)
                    }

                    override fun onCompleted() {
                        callback.onCompleted()
                    }

                    override fun onError(e: Throwable?) {
                        callback.onError(e)
                    }
                })

        return mApiLocalDataSource.getFoodsInBox(id)
    }

    fun updateBox(box: Box, callback: ApiRepositoryCallback<Box>) {
        mApiRemoteDataSource.updateBox(box, callback)
    }

    fun getFood(id: Int, callback: ApiRepositoryCallback<Food>) {
        mApiRemoteDataSource.getFood(id, callback)
    }

    fun createFood(name: String, notice: String, amount: Double, box: Box, unit: Unit, expirationDate: Date, callback: ApiRepositoryCallback<Food>) {
        mApiRemoteDataSource.createFood(name, notice, amount, box, unit, expirationDate, callback)
    }

    fun updateFood(food: Food, box: Box, callback: ApiRepositoryCallback<Food>) {
        mApiRemoteDataSource.updateFood(food, box, callback)
    }

    fun removeFood(id: Int, callback: ApiRepositoryCallback<Void>) {
        mApiRemoteDataSource.removeFood(id, callback)
    }

    fun getUnits(userId: Int, callback: ApiRepositoryCallback<List<Unit>>) {
        mApiRemoteDataSource.getUnits(userId, callback)
    }

    fun getUnit(id: Int, callback: ApiRepositoryCallback<Unit>) {
        mApiRemoteDataSource.getUnit(id, callback)
    }

    fun createUnit(label: String, step: Double, callback: ApiRepositoryCallback<Unit>) {
        mApiRemoteDataSource.createUnit(label, step, callback)
    }

    fun updateUnit(unit: Unit, callback: ApiRepositoryCallback<Unit>) {
        mApiRemoteDataSource.updateUnit(unit, callback)
    }

    fun removeUnit(id: Int, callback: ApiRepositoryCallback<Void>) {
        mApiRemoteDataSource.removeUnit(id, callback)
    }
}