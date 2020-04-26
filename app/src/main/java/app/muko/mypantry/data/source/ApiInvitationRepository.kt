package app.muko.mypantry.data.source

import app.muko.mypantry.data.source.remote.ApiRemoteInvitationSource
import retrofit2.Retrofit
import rx.Observable

class ApiInvitationRepository( retrofit: Retrofit) {

    private val mApiRemoteInvitationSource = ApiRemoteInvitationSource(retrofit)

    fun remove(id: Int): Observable<Void> {
        return mApiRemoteInvitationSource.remove(id)
    }
}