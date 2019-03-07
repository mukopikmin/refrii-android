package com.refrii.client.data.api.source

import com.refrii.client.data.api.models.Box
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

//    fun getFoods(callback: ApiRepositoryCallback<List<Food>>): List<Food> {
//        mApiRemoteDataSource.getFoods()
//                .subscribe(object: Subscriber<List<Food>>() {
//                    override fun onNext(t: List<Food>) {
//                        mApiLocalDataSource.saveFoods(t, box)
//                    }
//
//                    override fun onCompleted() {
//                        callback.onCompleted()
//                    }
//
//                    override fun onError(e: Throwable?) {
//                        callback.onError(e)
//                    }
//                })
//
//        return mApiLocalDataSource.getFoods()
//    }

    fun getFoodsInBox(box: Box, callback: ApiRepositoryCallback<List<Food>>): List<Food> {
        mApiRemoteDataSource.getFoodsInBox(box.id)
                .subscribe(object : Subscriber<List<Food>>() {
                    override fun onNext(t: List<Food>) {
                        mApiLocalDataSource.saveFoods(t, box)
                        callback.onNext(t)
                    }

                    override fun onCompleted() {
                        callback.onCompleted()
                    }

                    override fun onError(e: Throwable?) {
                        callback.onError(e)
                    }
                })

        return mApiLocalDataSource.getFoodsInBox(box.id)
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

    fun getUnits(userId: Int, callback: ApiRepositoryCallback<List<Unit>>): List<Unit> {
        mApiRemoteDataSource.getUnits()
                .subscribe(object : Subscriber<List<Unit>>() {
                    override fun onNext(t: List<Unit>) {
                        mApiLocalDataSource.saveUnits(t, userId)
                        callback.onNext(t)
                    }

                    override fun onCompleted() {
                        callback.onCompleted()
                    }

                    override fun onError(e: Throwable?) {
                        callback.onError(e)
                    }
                })

        return mApiLocalDataSource.getUnits(userId)
    }

    fun getUnit(id: Int, callback: ApiRepositoryCallback<Unit>): Unit? {
        mApiRemoteDataSource.getUnit(id)
                .subscribe(object : Subscriber<Unit>() {
                    override fun onNext(t: Unit) {
                        mApiLocalDataSource.saveUnit(t)
                        callback.onNext(t)
                    }

                    override fun onCompleted() {
                        callback.onCompleted()
                    }

                    override fun onError(e: Throwable?) {
                        callback.onError(e)
                    }
                })

        return mApiLocalDataSource.getUnit(id)
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