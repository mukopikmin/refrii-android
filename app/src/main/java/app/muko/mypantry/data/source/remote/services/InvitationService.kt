package app.muko.mypantry.data.source.remote.services

import retrofit2.http.DELETE
import retrofit2.http.Path
import rx.Observable

interface InvitationService {
    @DELETE("/invitations/{id}")
    fun remove(@Path("id") id: Int): Observable<Void>
}
