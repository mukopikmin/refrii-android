package com.refrii.client.data.source

import com.refrii.client.data.models.User
import com.refrii.client.data.source.local.ApiLocalUserSource
import com.refrii.client.data.source.remote.ApiRemoteUserSource
import io.realm.Realm
import retrofit2.Retrofit
import rx.Observable

class ApiUserRepository(realm: Realm, retrofit: Retrofit) {

    private val mAPiRemoteUserSource = ApiRemoteUserSource(retrofit)
    private val mApiLocalUserSource = ApiLocalUserSource(realm)

    fun signup(): Observable<User> {
        return mAPiRemoteUserSource.signup()
                .flatMap { mApiLocalUserSource.saveUser(it) }
    }

    fun verify(): Observable<User> {
        return mAPiRemoteUserSource.verify()
                .flatMap { mApiLocalUserSource.saveUser(it) }
    }

    fun registerPushToken(id: Int, token: String?): Observable<User> {
        return mAPiRemoteUserSource.registerPushToken(id, token)
    }

    fun deleteLocalData() {
        mApiLocalUserSource.deleteAll()
    }
}