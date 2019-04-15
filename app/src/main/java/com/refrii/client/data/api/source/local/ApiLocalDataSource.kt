package com.refrii.client.data.api.source.local

import com.refrii.client.data.api.models.Box
import com.refrii.client.data.api.models.Food
import com.refrii.client.data.api.models.Unit
import com.refrii.client.data.api.models.User
import io.realm.Realm
import io.realm.kotlin.oneOf
import io.realm.kotlin.where
import rx.Observable
import java.util.*

class ApiLocalDataSource(private val mRealm: Realm) {

    fun getBoxes(): Observable<List<Box>> {
        val boxes = mRealm.where(Box::class.java)
                .findAll()
                .sort("id")

        return Observable.just(boxes)
    }

    fun saveBoxes(boxes: List<Box>): Observable<List<Box>> {
        mRealm.executeTransaction { realm ->
            val onlyLocal = realm.where<Box>()
                    .not().oneOf("id", boxes.map { it.id }.toTypedArray())
                    .findAll()

            onlyLocal.deleteAllFromRealm()
            realm.copyToRealmOrUpdate(boxes)
        }

        return getBoxes()
    }

    fun saveBox(box: Box): Observable<Box?> {
        mRealm.executeTransaction {
            it.copyToRealmOrUpdate(box)
        }

        return getBox(box.id)
    }

    fun getUnitsForBox(id: Int): Observable<List<Unit>> {
        val box = mRealm.where<Box>()
                .equalTo("id", id)
                .findFirst()
        val user = mRealm.where<User>()
                .equalTo("id", box?.owner?.id)
                .findFirst()
        val units = mRealm.where<Unit>()
                .equalTo("user.id", user?.id)
                .findAll()
                .sort("id")

        return Observable.just(units)
    }

    fun getFoods(): Observable<List<Food>> {
        val foods = mRealm.where<Food>()
                .findAll()
                .sort("id")

        return Observable.just(foods)
    }

    fun getExpiringFoods(): Observable<List<Food>> {
        val week = 7 * 24 * 60 * 60 * 1000 // 7 days
        val now = Date().time
        val foods = mRealm.where<Food>()
                .findAll()
                .filter { it.expirationDate!!.time - now < week }
                .sortedBy { it.expirationDate }

        return Observable.just(foods)
    }

    fun saveFoods(foods: List<Food>): Observable<List<Food>> {
        mRealm.executeTransaction { realm ->
            realm.where<Food>()
                    .not().oneOf("id", foods.map { it.id }.toTypedArray())
                    .findAll()
                    .deleteAllFromRealm()
            realm.copyToRealmOrUpdate(foods)
        }

        return getFoods()
    }

    fun updateFood(id: Int, name: String?, notice: String?, amount: Double?, expirationDate: Date?, boxId: Int?, unitId: Int?): Observable<Food> {
        val food = mRealm.where<Food>()
                .equalTo("id", id)
                .findFirst() ?: return Observable.error(Throwable("見つかりませんでした"))

        mRealm.executeTransaction { realm ->
            val box = realm.where<Box>()
                    .equalTo("id", boxId)
                    .findFirst()
            val unit = realm.where<Unit>()
                    .equalTo("id", unitId)
                    .findFirst()

            name?.let { food.name = it }
            notice?.let { food.notice = it }
            amount?.let { food.amount = it }
            expirationDate?.let { food.expirationDate = it }
            box?.let { food.box = box }
            unit?.let { food.unit = unit }
        }

        return Observable.just(food)
    }

    fun saveFood(food: Food): Observable<Food?> {
        mRealm.executeTransaction {
            it.copyToRealmOrUpdate(food)
        }

        return getFood(food.id)
    }

    fun getBox(id: Int): Observable<Box?> {
        val box = mRealm.where<Box>()
                .equalTo("id", id)
                .findFirst()

        return Observable.just(box)
    }

    fun updateBox(box: Box) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun removeBox(id: Int): Observable<Void> {
        mRealm.executeTransaction { realm ->
            realm.where<Box>()
                    .equalTo("id", id)
                    .findAll()
                    .deleteAllFromRealm()
        }

        return Observable.empty()
    }

    fun getFoodsInBox(id: Int): Observable<List<Food>> {
        val foods = mRealm.where<Food>()
                .equalTo("box.id", id)
                .findAll()
                .sort("id")

        return Observable.just(foods)
    }

    fun getFood(id: Int): Observable<Food?> {
        val food = mRealm.where<Food>()
                .equalTo("id", id)
                .findFirst()

        return Observable.just(food)
    }

    fun createFood(name: String, notice: String, amount: Double, box: Box, unit: Unit, expirationDate: Date) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun removeFood(id: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun getUnits(userId: Int): Observable<List<Unit>> {
        val units = mRealm.where<Unit>()
                .equalTo("user.id", userId)
                .findAll()
                .sort("id")
                .filter { it.user?.id == userId }

        return Observable.just(units)
    }

    fun getUnit(id: Int): Observable<Unit?> {
        val unit = mRealm.where<Unit>()
                .equalTo("id", id)
                .findFirst()

        return Observable.just(unit)
    }

    fun saveUnits(units: List<Unit>): Observable<List<Unit>> {
        val userId = units.first().user?.id ?: return Observable.empty()

        mRealm.executeTransaction { realm ->
            val onlyLocal = realm.where<Unit>()
                    .equalTo("user.id", userId)
                    .not().oneOf("id", units.map { it.id }.toTypedArray())
                    .findAll()

            onlyLocal.deleteAllFromRealm()
            realm.copyToRealmOrUpdate(units)
        }

        return getUnits(userId)
    }

    fun saveUnit(unit: Unit): Observable<Unit?> {
        mRealm.executeTransaction {
            it.copyToRealmOrUpdate(unit)
        }

        return getUnit(unit.id)
    }

    fun createUnit(label: String, step: Double) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun updateUnit(unit: Unit) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun removeUnit(id: Int): Observable<Void> {
        mRealm.executeTransaction {
            it.where(Unit::class.java)
                    .equalTo("id", id)
                    .findFirst()
                    ?.deleteFromRealm()
        }

        return Observable.empty()
    }

    fun getUser(id: Int): Observable<User?> {
        val user = mRealm.where<User>()
                .equalTo("id", id)
                .findFirst()

        return Observable.just(user)
    }

    fun saveUser(user: User): Observable<User?> {
        mRealm.executeTransaction {
            it.copyToRealmOrUpdate(user)
        }

        return getUser(user.id)
    }

    fun deleteAll() {
        mRealm.executeTransaction {
            it.deleteAll()
        }
    }
}