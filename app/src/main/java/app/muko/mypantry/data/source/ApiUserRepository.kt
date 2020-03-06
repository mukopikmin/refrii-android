package app.muko.mypantry.data.source

import app.muko.mypantry.data.models.User
import app.muko.mypantry.data.source.local.ApiLocalUserSource
import app.muko.mypantry.data.source.remote.ApiRemoteUserSource
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

    fun update(id: Int, name: String?): Observable<User> {
        return mAPiRemoteUserSource.update(id, name)
    }

    fun deleteLocalData() {
        mApiLocalUserSource.deleteAll()
    }
}