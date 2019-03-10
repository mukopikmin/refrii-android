package com.refrii.client.data.api.source.local

import com.refrii.client.data.api.models.Box
import com.refrii.client.data.api.models.Food
import com.refrii.client.data.api.models.Unit
import com.refrii.client.data.api.models.User
import com.refrii.client.data.api.source.ApiRepositoryCallback
import io.realm.Realm
import io.realm.kotlin.oneOf
import io.realm.kotlin.where
import java.util.*

class ApiLocalDataSource(private val mRealm: Realm) {

//    private var mRealm: Realm

//    init {
//        Realm.init(context)
//        mRealm = RealmUtil.getInstance()
//    }

    fun getBoxes(): List<Box> {
        return mRealm.where(Box::class.java)
                .findAll()
                .sort("id")
    }

    fun saveBoxes(boxes: List<Box>) {
        mRealm.executeTransaction { realm ->
            //            val success = realm.where<Box>()
////                    .oneOf("id", boxes.map { it.id }.toTypedArray())
//                    .findAll()
//
//            Log.e("AAAAAAAAAAAAAAAAAAA", success.toString())
//            val result=success.deleteAllFromRealm()
//            Log.e("AAAAAAAAAAAAAAAAAAA", result.toString())

            realm.copyToRealmOrUpdate(boxes)
        }
    }

    fun getFoods(): List<Food> {
        return mRealm.where<Food>().findAll()
    }

    fun saveFoods(foods: List<Food>, box: Box) {
        mRealm.executeTransaction { realm ->
            val onlyLocal = realm.where<Food>()
                    .equalTo("box.id", box.id)
                    .and()
                    .not().oneOf("id", foods.map { it.id }.toTypedArray())
                    .findAll()

            onlyLocal.deleteAllFromRealm()
            foods.forEach {
                it.box = box
                realm.copyToRealmOrUpdate(foods)
            }
        }
    }

    fun getBox(id: Int): Box? {
        return mRealm.where<Box>()
                .equalTo("id", id)
                .findFirst()
    }

    fun updateBox(box: Box, callback: ApiRepositoryCallback<Box>) {
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

    fun createFood(name: String, notice: String, amount: Double, box: Box, unit: Unit, expirationDate: Date, callback: ApiRepositoryCallback<Food>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun updateFood(food: Food, box: Box, callback: ApiRepositoryCallback<Food>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun removeFood(id: Int, callback: ApiRepositoryCallback<Void>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun getUnits(userId: Int): List<Unit> {
        return mRealm.where<Unit>()
                .equalTo("user.id", userId)
//                .or()
//                .isNull("user")
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

    fun createUnit(label: String, step: Double, callback: ApiRepositoryCallback<Unit>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun updateUnit(unit: Unit, callback: ApiRepositoryCallback<Unit>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun removeUnit(id: Int, callback: ApiRepositoryCallback<Void>) {
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

//    private fun <T> getSubscriber(callback: ApiRepositoryCallback<T>): Subscriber<T> {
//        return object : Subscriber<T>() {
//            fun onNext(t: T) {
//                callback.onNext(t)
//            }
//
//            fun onCompleted() {
//                callback.onCompleted()
//            }
//
//            fun onError(e: Throwable?) {
//                callback.onError(e)
//            }
//        }
//    }
}