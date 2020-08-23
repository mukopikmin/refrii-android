package app.muko.mypantry.data.source.remote.services

import io.reactivex.Completable
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.Path

interface InvitationService {

    @POST("/boxes/{id}/invitations")
    fun create(
            @Path("id") id: Int,
            @Body body: RequestBody): Completable

    @DELETE("/invitations/{id}")
    fun remove(@Path("id") id: Int): Completable
}
