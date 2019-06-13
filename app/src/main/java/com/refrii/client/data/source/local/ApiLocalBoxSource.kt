package com.refrii.client.data.source.local

import com.refrii.client.data.models.Box
import com.refrii.client.data.models.Food
import com.refrii.client.data.models.Unit
import com.refrii.client.data.models.User
import io.realm.Realm
import io.realm.kotlin.oneOf
import io.realm.kotlin.where
import rx.Observable

class ApiLocalBoxSource(private val mRealm: Realm) {

    fun getBoxes(): Observable<List<Box>> {
        val boxes = mRealm.where(Box::class.java)
                .findAll()
                .sort("id")

        return Observable.just(mRealm.copyFromRealm(boxes))
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

        return Observable.just(mRealm.copyFromRealm(units))
    }

    fun getBox(id: Int): Observable<Box?> {
        val box = mRealm.where<Box>()
                .equalTo("id", id)
                .findFirst()

        return Observable.just(mRealm.copyFromRealm(box))
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

        return Observable.just(mRealm.copyFromRealm(foods))
    }
}