package app.muko.mypantry.data.source.remote

import app.muko.mypantry.data.source.remote.services.InvitationService
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit

class ApiRemoteInvitationSource(private val mRetrofit: Retrofit) {

    fun remove(id: Int): Flowable<Void> {
        return mRetrofit.create(InvitationService::class.java)
                .remove(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }
}
