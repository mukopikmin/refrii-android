package com.refrii.client.data.api.source.local

import com.refrii.client.data.api.models.Box
import com.refrii.client.data.api.models.Food
import com.refrii.client.data.api.models.Unit
import com.refrii.client.data.api.models.User
import io.realm.Realm
import io.realm.kotlin.oneOf
import io.realm.kotlin.where
import java.util.*

class ApiLocalDataSource(private val mRealm: Realm) {

    fun getBoxes(): List<Box> {
        return mRealm.where(Box::class.java)
                .findAll()
                .sort("id")
    }

    fun saveBoxes(boxes: List<Box>) {
        mRealm.executeTransaction { realm ->
            val onlyLocal = realm.where<Box>()
                    .not().oneOf("id", boxes.map { it.id }.toTypedArray())
                    .findAll()

            onlyLocal.deleteAllFromRealm()
            realm.copyToRealmOrUpdate(boxes)
        }
    }

    fun saveBox(box: Box) {
        mRealm.executeTransaction {
            it.copyToRealmOrUpdate(box)
        }
    }

    fun getFoods(): List<Food> {
        return mRealm.where<Food>().findAll()
    }

    fun getExpiringFoods(): List<Food> {
        val week = 7 * 24 * 60 * 60 * 1000 // 7 days
        val now = Date().time

        return mRealm.where<Food>()
                .findAll()
                .filter { it.expirationDate!!.time - now < week }
                .sortedBy { it.expirationDate }
    }

//    fun saveFoods(foods: List<Food>, box: Box) {
//        mRealm.executeTransaction { realm ->
//            realm.where<Food>()
//                    .equalTo("box.id", box.id)
//                    .and()
//                    .not().oneOf("id", foods.map { it.id }.toTypedArray())
//                    .findAll()
//                    .deleteAllFromRealm()
//
//            foods.forEach {
//                it.box = box
//                realm.copyToRealmOrUpdate(foods)
//            }
//        }
//    }

    fun saveFoods(foods: List<Food>) {
        mRealm.executeTransaction { realm ->
            realm.where<Food>()
                    .not().oneOf("id", foods.map { it.id }.toTypedArray())
                    .findAll()
                    .deleteAllFromRealm()
            realm.copyToRealmOrUpdate(foods)
        }
    }

    fun updateFood(id: Int, name: String? = null, notice: String? = null, amount: Double? = null, expirationDate: Date? = null) {
        val food = mRealm.where<Food>()
                .equalTo("id", id)
                .findFirst() ?: return

        mRealm.executeTransaction {
            name?.let { food.name = it }
            notice?.let { food.notice = it }
            amount?.let { food.amount = it }
            expirationDate?.let { food.expirationDate = it }
        }
    }

    fun saveFood(food: Food) {
        mRealm.executeTransaction {
            it.copyToRealmOrUpdate(food)
        }
    }

    fun getBox(id: Int): Box? {
        return mRealm.where<Box>()
                .equalTo("id", id)
                .findFirst()
    }

    fun updateBox(box: Box) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun getFoodsInBox(id: Int): List<Food> {
        return mRealm.where<Food>()
                .equalTo("box.id", id)
                .findAll()
                .sort("id")
    }

    fun getFood(id: Int): Food? {
        return mRealm.where<Food>()
                .equalTo("id", id)
                .findFirst()
    }

    fun createFood(name: String, notice: String, amount: Double, box: Box, unit: Unit, expirationDate: Date) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun updateFood(food: Food, box: Box) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun removeFood(id: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun getUnits(userId: Int): List<Unit> {
        return mRealm.where<Unit>()
                .equalTo("user.id", userId)
                .findAll()
                .sort("id")
                .filter { it.user?.id == userId }
    }

    fun getUnit(id: Int): Unit? {
        return mRealm.where<Unit>()
                .equalTo("id", id)
                .findFirst()
    }

    fun saveUnits(units: List<Unit>, userId: Int) {
        mRealm.executeTransaction { realm ->
            val onlyLocal = realm.where<Unit>()
                    .equalTo("user.id", userId)
                    .not().oneOf("id", units.map { it.id }.toTypedArray())
                    .findAll()

            onlyLocal.deleteAllFromRealm()
            realm.copyToRealmOrUpdate(units)
        }
    }

    fun saveUnit(unit: Unit) {
        mRealm.executeTransaction {
            it.copyToRealmOrUpdate(unit)
        }
    }

    fun createUnit(label: String, step: Double) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun updateUnit(unit: Unit) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun removeUnit(id: Int) {
        mRealm.executeTransaction {
            it.where(Unit::class.java)
                    .equalTo("id", id)
                    .findFirst()
                    ?.deleteFromRealm()
        }
    }

    fun saveUser(user: User) {
        mRealm.executeTransaction {
            it.copyToRealmOrUpdate(user)
        }
    }

    fun deleteAll() {
        mRealm.executeTransaction {
            it.deleteAll()
        }
    }
}