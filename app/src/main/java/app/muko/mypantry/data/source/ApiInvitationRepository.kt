package app.muko.mypantry.data.source

import app.muko.mypantry.data.source.remote.ApiRemoteInvitationSource
import io.reactivex.Flowable
import retrofit2.Retrofit

class ApiInvitationRepository( retrofit: Retrofit) {

    private val mApiRemoteInvitationSource = ApiRemoteInvitationSource(retrofit)

    fun remove(id: Int): Flowable<Void> {
        return mApiRemoteInvitationSource.remove(id)
    }
}