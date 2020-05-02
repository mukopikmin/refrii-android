package app.muko.mypantry.data.source

import app.muko.mypantry.data.models.User
import app.muko.mypantry.data.source.remote.ApiRemoteUserSource
import io.reactivex.Flowable
import retrofit2.Retrofit

class ApiUserRepository(retrofit: Retrofit) {

    private val mAPiRemoteUserSource = ApiRemoteUserSource(retrofit)

    fun signup(): Flowable<User> {
        return mAPiRemoteUserSource.signup()
    }

    fun verify(): Flowable<User> {
        return mAPiRemoteUserSource.verify()
    }

    fun registerPushToken(id: Int, token: String?): Flowable<User> {
        return mAPiRemoteUserSource.registerPushToken(id, token)
    }

    fun update(id: Int, name: String?): Flowable<User> {
        return mAPiRemoteUserSource.update(id, name)
    }

//    fun deleteLocalData() {
//        mApiLocalUserSource.deleteAll()
//    }
}