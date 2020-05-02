package app.muko.mypantry.data.source.remote.services

import io.reactivex.Flowable
import retrofit2.http.DELETE
import retrofit2.http.Path

interface InvitationService {
    @DELETE("/invitations/{id}")
    fun remove(@Path("id") id: Int): Flowable<Void>
}
