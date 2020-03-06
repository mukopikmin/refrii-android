package app.muko.mypantry.data.source.remote

import app.muko.mypantry.data.source.remote.services.InvitationService
import retrofit2.Retrofit
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class ApiRemoteInvitationSource(private val mRetrofit: Retrofit) {

    fun remove(id: Int): Observable<Void> {
        return mRetrofit.create(InvitationService::class.java)
                .remove(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }
}
