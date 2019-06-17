package com.refrii.client.data.source.local

import com.refrii.client.data.models.Unit
import io.realm.Realm
import io.realm.kotlin.where
import rx.Observable

class ApiLocalUnitSource(private val mRealm: Realm) {
    fun getUnits(userId: Int): Observable<List<Unit>> {
        val units = mRealm.where<Unit>()
                .equalTo("user.id", userId)
                .findAll()
                .sort("id")
                .filter { it.user?.id == userId }

        return Observable.just(mRealm.copyFromRealm(units))
    }

    fun getUnit(id: Int): Observable<Unit?> {
        val unit = mRealm.where<Unit>()
                .equalTo("id", id)
                .findFirst()

        return Observable.just(mRealm.copyFromRealm(unit))
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
}