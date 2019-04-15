package com.refrii.client.data.api.source

import com.refrii.client.data.api.models.*
import com.refrii.client.data.api.models.Unit
import com.refrii.client.data.api.source.local.ApiLocalDataSource
import com.refrii.client.data.api.source.remote.ApiRemoteDataSource
import io.realm.Realm
import retrofit2.Retrofit
import rx.Observable
import java.util.*

class ApiRepository(realm: Realm, retrofit: Retrofit) {

    private val mApiRemoteDataSource = ApiRemoteDataSource(retrofit)
    private val mApiLocalDataSource = ApiLocalDataSource(realm)

    fun verify(): Observable<User> {
        return mApiRemoteDataSource.verify()
                .flatMap { mApiLocalDataSource.saveUser(it) }
    }

    fun getBoxes(): Observable<List<Box>> {
        return mApiRemoteDataSource.getBoxes()
                .flatMap { mApiLocalDataSource.saveBoxes(it) }
    }

    fun getBoxesFromCache(): Observable<List<Box>> {
        return mApiLocalDataSource.getBoxes()
    }

    fun getBox(id: Int): Observable<Box?> {
        return mApiRemoteDataSource.getBox(id)
                .flatMap { mApiLocalDataSource.saveBox(it) }
    }

    fun getBoxFromCache(id: Int): Observable<Box?> {
        return mApiLocalDataSource.getBox(id)
    }

    fun createBox(name: String, notice: String?): Observable<Box> {
        return mApiRemoteDataSource.createBox(name, notice)
                .flatMap { mApiLocalDataSource.saveBox(it) }
    }

    fun getUnitsForBox(id: Int): Observable<List<Unit>> {
        return mApiRemoteDataSource.getUnitsForBox(id)
                .flatMap { mApiLocalDataSource.saveUnits(it) }
    }

    fun getUnitsForBoxFromCache(id: Int): Observable<List<Unit>> {
        return mApiLocalDataSource.getUnitsForBox(id)
    }

    fun getFoods(): Observable<List<Food>> {
        return mApiRemoteDataSource.getFoods()
                .flatMap { mApiLocalDataSource.saveFoods(it) }
    }

    fun getFoodsInBox(id: Int): Observable<List<Food>> {
        return mApiRemoteDataSource.getFoodsInBox(id)
                .flatMap { mApiLocalDataSource.saveFoods(it) }
    }

    fun getFoodsInBoxFromCache(id: Int): Observable<List<Food>> {
        return mApiLocalDataSource.getFoodsInBox(id)
    }

    fun getFoodsFromCache(): Observable<List<Food>> {
        return mApiLocalDataSource.getFoods()
    }

    fun updateBox(id: Int, name: String?, notice: String?): Observable<Box> {
        return mApiRemoteDataSource.updateBox(id, name, notice)
                .flatMap { mApiLocalDataSource.saveBox(it) }
    }

    fun removeBox(id: Int): Observable<Void> {
        return mApiRemoteDataSource.removeBox(id)
                .flatMap { mApiLocalDataSource.removeBox(id) }
    }

    fun invite(boxId: Int, email: String): Observable<Invitation> {
        return mApiRemoteDataSource.invite(boxId, email)
    }

    fun uninvite(boxId: Int, email: String): Observable<Void> {
        return mApiRemoteDataSource.uninvite(boxId, email)
    }

    fun getFood(id: Int): Observable<Food?> {
        return mApiRemoteDataSource.getFood(id)
                .flatMap { mApiLocalDataSource.saveFood(it) }
    }

    fun getExpiringFoods(): Observable<List<Food>> {
        return mApiRemoteDataSource.getFoods()
                .flatMap { mApiLocalDataSource.saveFoods(it) }
    }

    fun getExpiringFoodsFromCache(): Observable<List<Food>> {
        return mApiLocalDataSource.getExpiringFoods()
    }

    fun createFood(name: String, notice: String, amount: Double, box: Box, unit: Unit, expirationDate: Date): Observable<Food> {
        return mApiRemoteDataSource.createFood(name, notice, amount, box, unit, expirationDate)
                .flatMap { mApiLocalDataSource.saveFood(it) }
    }

    fun updateFood(id: Int, name: String?, notice: String?, amount: Double?, expirationDate: Date?, boxId: Int?, unitId: Int?): Observable<Food> {
        return mApiRemoteDataSource.updateFood(id, name, notice, amount, expirationDate, boxId, unitId)
                .flatMap { mApiLocalDataSource.updateFood(it.id, it.name, it.notice, it.amount, it.expirationDate, it.box?.id, it.unit?.id) }
    }

    fun removeFood(id: Int): Observable<Void> {
        return mApiRemoteDataSource.removeFood(id)
    }

    fun getUnits(userId: Int): Observable<List<Unit>> {
        return mApiRemoteDataSource.getUnits()
                .flatMap { mApiLocalDataSource.saveUnits(it) }
    }

    fun getUnit(id: Int): Observable<Unit?> {
        return mApiRemoteDataSource.getUnit(id)
                .flatMap { mApiLocalDataSource.saveUnit(it) }
    }

    fun createUnit(label: String, step: Double): Observable<Unit> {
        return mApiRemoteDataSource.createUnit(label, step)
                .flatMap { mApiLocalDataSource.saveUnit(it) }
    }

    fun updateUnit(unit: Unit): Observable<Unit> {
        return mApiRemoteDataSource.updateUnit(unit)
                .flatMap { mApiLocalDataSource.saveUnit(it) }
    }

    fun removeUnit(id: Int): Observable<Void> {
        return mApiRemoteDataSource.removeUnit(id)
    }

    fun registerPushToken(id: Int, token: String?): Observable<User> {
        return mApiRemoteDataSource.registerPushToken(id, token)
    }

    fun deleteLocalData() {
        mApiLocalDataSource.deleteAll()
    }
}