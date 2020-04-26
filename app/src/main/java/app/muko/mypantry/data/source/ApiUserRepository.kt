package app.muko.mypantry.data.source

import app.muko.mypantry.data.models.User
import app.muko.mypantry.data.source.remote.ApiRemoteUserSource
import retrofit2.Retrofit
import rx.Observable

class ApiUserRepository(retrofit: Retrofit) {

    private val mAPiRemoteUserSource = ApiRemoteUserSource(retrofit)

    fun signup(): Observable<User> {
        return mAPiRemoteUserSource.signup()
    }

    fun verify(): Observable<User> {
        return mAPiRemoteUserSource.verify()
    }

    fun registerPushToken(id: Int, token: String?): Observable<User> {
        return mAPiRemoteUserSource.registerPushToken(id, token)
    }

    fun update(id: Int, name: String?): Observable<User> {
        return mAPiRemoteUserSource.update(id, name)
    }

//    fun deleteLocalData() {
//        mApiLocalUserSource.deleteAll()
//    }
}