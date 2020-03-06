package app.muko.mypantry.data.source

import app.muko.mypantry.data.source.remote.ApiRemoteNoticeSource
import io.realm.Realm
import retrofit2.Retrofit
import rx.Observable

class ApiNoticeRepository(realm: Realm, retrofit: Retrofit) {

    private val mApiRemoteNoticeSource = ApiRemoteNoticeSource(retrofit)

    fun remove(id: Int): Observable<Void> {
        return mApiRemoteNoticeSource.remove(id)
    }
}