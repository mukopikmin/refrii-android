package com.refrii.client.data.source.local

import com.refrii.client.data.models.User
import io.realm.Realm
import io.realm.kotlin.where
import rx.Observable

class ApiLocalUserSource(private val mRealm: Realm) {
    fun getUser(id: Int): Observable<User?> {
        val user = mRealm.where<User>()
                .equalTo("id", id)
                .findFirst()

        return Observable.just(mRealm.copyFromRealm(user))
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