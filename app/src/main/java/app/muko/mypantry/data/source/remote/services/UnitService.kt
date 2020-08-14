package app.muko.mypantry.data.source.remote.services

import app.muko.mypantry.data.models.Unit
import io.reactivex.Completable
import io.reactivex.Flowable
import okhttp3.RequestBody
import retrofit2.http.*

interface UnitService {

    @GET("/units")
    fun getAll(): Flowable<List<Unit>>

    @GET("/boxes/{boxId}/units")
    fun getByBox(@Path("boxId") boxId: Int): Flowable<List<Unit>>

    @GET("/units/{id}")
    fun get(@Path("id") id: Int): Flowable<Unit>

    @POST("/units")
    fun create(@Body body: RequestBody): Completable

    @PUT("/units/{id}")
    fun update(
            @Path("id") id: Int,
            @Body body: RequestBody): Completable

    @DELETE("/units/{id}")
    fun remove(@Path("id") id: Int): Completable
}
