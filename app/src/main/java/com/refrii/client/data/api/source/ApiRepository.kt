package com.refrii.client.data.api.source

import com.refrii.client.data.api.models.*
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

    fun verify(callback: ApiRepositoryCallback<User>) {
        mApiRemoteDataSource.verify()
                .subscribe(object : Subscriber<User>() {
                    override fun onNext(t: User) {
                        mApiLocalDataSource.saveUser(t)
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

    fun getBoxes(callback: ApiRepositoryCallback<List<Box>>): List<Box> {
        mApiRemoteDataSource.getBoxes()
                .subscribe(object : Subscriber<List<Box>>() {
                    override fun onNext(t: List<Box>) {
                        mApiLocalDataSource.saveBoxes(t)
                        callback.onNext(mApiLocalDataSource.getBoxes())
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

    fun getBox(id: Int, callback: ApiRepositoryCallback<Box>): Box? {
        mApiRemoteDataSource.getBox(id)
                .subscribe(object : Subscriber<Box>() {
                    override fun onNext(t: Box) {
                        mApiLocalDataSource.saveBox(t)
                        callback.onNext(mApiLocalDataSource.getBox(id))
                    }

                    override fun onCompleted() {
                        callback.onCompleted()
                    }

                    override fun onError(e: Throwable) {
                        callback.onError(e)
                    }
                })

        return mApiLocalDataSource.getBox(id)
    }

    fun createBox(callback: ApiRepositoryCallback<Box>, name: String, notice: String?) {
        mApiRemoteDataSource.createBox(name, notice)
                .subscribe(object : Subscriber<Box>() {
                    override fun onNext(t: Box) {
                        mApiLocalDataSource.saveBox(t)
                        callback.onNext(mApiLocalDataSource.getBox(t.id))
                    }

                    override fun onCompleted() {
                        callback.onCompleted()
                    }

                    override fun onError(e: Throwable?) {
                        callback.onError(e)
                    }
                })
    }

    fun getUnitsForBox(id: Int, callback: ApiRepositoryCallback<List<Unit>>): List<Unit> {
        mApiRemoteDataSource.getUnitsForBox(id)
                .subscribe(object : Subscriber<List<Unit>>() {
                    override fun onNext(t: List<Unit>) {
                        mApiLocalDataSource.saveUnits(t)
                        callback.onNext(mApiLocalDataSource.getUnitsForBox(id))
                    }

                    override fun onCompleted() {
                        callback.onCompleted()
                    }

                    override fun onError(e: Throwable?) {
                        callback.onError(e)
                    }
                })

        return mApiLocalDataSource.getUnitsForBox(id)
    }

    fun getFoods(box: Box, callback: ApiRepositoryCallback<List<Food>>): List<Food> {
        mApiRemoteDataSource.getFoods()
                .subscribe(object : Subscriber<List<Food>>() {
                    override fun onNext(t: List<Food>) {
                        mApiLocalDataSource.saveFoods(t)
                        callback.onNext(mApiLocalDataSource.getFoodsInBox(box.id))
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

    fun updateBox(callback: ApiRepositoryCallback<Box>, id: Int, name: String?, notice: String?) {
        mApiRemoteDataSource.updateBox(id, name, notice)
                .subscribe(object : Subscriber<Box>() {
                    override fun onNext(t: Box) {
                        mApiLocalDataSource.saveBox(t)
                        callback.onNext(mApiLocalDataSource.getBox(t.id))
                    }

                    override fun onCompleted() {
                        callback.onCompleted()
                    }

                    override fun onError(e: Throwable?) {
                        callback.onError(e)
                    }
                })
    }

    fun removeBox(callback: ApiRepositoryCallback<Void>, id: Int) {
        mApiRemoteDataSource.removeBox(id)
                .subscribe(object : Subscriber<Void>() {
                    override fun onNext(t: Void?) {
                        mApiLocalDataSource.removeBox(id)
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

    fun invite(callback: ApiRepositoryCallback<Invitation>, boxId: Int, email: String) {
        mApiRemoteDataSource.invite(boxId, email)
                .subscribe(object : Subscriber<Invitation>() {
                    override fun onNext(t: Invitation?) {
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

    fun getFood(id: Int, callback: ApiRepositoryCallback<Food>): Food? {
        mApiRemoteDataSource.getFood(id)
                .subscribe(object : Subscriber<Food>() {
                    override fun onNext(t: Food) {
                        mApiLocalDataSource.saveFood(t)
                        callback.onNext(mApiLocalDataSource.getFood(id))
                    }

                    override fun onCompleted() {
                        callback.onCompleted()
                    }

                    override fun onError(e: Throwable?) {
                        callback.onError(e)
                    }
                })

        return mApiLocalDataSource.getFood(id)
    }

    fun getExpiringFoods(callback: ApiRepositoryCallback<List<Food>>): List<Food> {
        mApiRemoteDataSource.getFoods()
                .subscribe(object : Subscriber<List<Food>>() {
                    override fun onNext(t: List<Food>) {
                        mApiLocalDataSource.saveFoods(t)
                        callback.onNext(mApiLocalDataSource.getExpiringFoods())
                    }

                    override fun onCompleted() {
                        callback.onCompleted()
                    }

                    override fun onError(e: Throwable) {
                        callback.onError(e)
                    }
                })

        return mApiLocalDataSource.getExpiringFoods()
    }

    fun createFood(name: String, notice: String, amount: Double, box: Box, unit: Unit, expirationDate: Date, callback: ApiRepositoryCallback<Food>) {
        mApiRemoteDataSource.createFood(name, notice, amount, box, unit, expirationDate)
                .subscribe(object : Subscriber<Food>() {
                    override fun onNext(t: Food) {
                        mApiLocalDataSource.saveFood(t)
                        callback.onNext(mApiLocalDataSource.getFood(t.id))
                    }

                    override fun onCompleted() {
                        callback.onCompleted()
                    }

                    override fun onError(e: Throwable?) {
                        callback.onError(e)
                    }
                })
    }

    fun updateFood(callback: ApiRepositoryCallback<Food>, id: Int, name: String?, notice: String?, amount: Double?, expirationDate: Date?, boxId: Int?, unitId: Int?) {
        mApiRemoteDataSource.updateFood(id, name, notice, amount, expirationDate, boxId, unitId)
                .subscribe(object : Subscriber<Food>() {
                    override fun onNext(t: Food) {
                        mApiLocalDataSource.updateFood(t.id, t.name, t.notice, t.amount, t.expirationDate, t.box?.id, t.unit?.id)
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

    fun removeFood(id: Int, callback: ApiRepositoryCallback<Void>) {
        mApiRemoteDataSource.removeFood(id)
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

    fun getUnits(userId: Int, callback: ApiRepositoryCallback<List<Unit>>): List<Unit> {
        mApiRemoteDataSource.getUnits()
                .subscribe(object : Subscriber<List<Unit>>() {
                    override fun onNext(t: List<Unit>) {
                        mApiLocalDataSource.saveUnits(t)
                        callback.onNext(mApiLocalDataSource.getUnits(userId))
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
        mApiRemoteDataSource.createUnit(label, step)
                .subscribe(object : Subscriber<Unit>() {
                    override fun onNext(t: Unit) {
                        mApiLocalDataSource.saveUnit(t)
                        callback.onNext(mApiLocalDataSource.getUnit(t.id))
                    }

                    override fun onCompleted() {
                        callback.onCompleted()
                    }

                    override fun onError(e: Throwable?) {
                        callback.onError(e)
                    }
                })
    }

    fun updateUnit(unit: Unit, callback: ApiRepositoryCallback<Unit>) {
        mApiRemoteDataSource.updateUnit(unit)
                .subscribe(object : Subscriber<Unit>() {
                    override fun onNext(t: Unit) {
                        mApiLocalDataSource.saveUnit(t)
                        callback.onNext(mApiLocalDataSource.getUnit(unit.id))
                    }

                    override fun onCompleted() {
                        callback.onCompleted()
                    }

                    override fun onError(e: Throwable?) {
                        callback.onError(e)
                    }
                })
    }

    fun removeUnit(id: Int, callback: ApiRepositoryCallback<Void>) {
        mApiRemoteDataSource.removeUnit(id)
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

    fun deleteLocalData() {
        mApiLocalDataSource.deleteAll()
    }
}