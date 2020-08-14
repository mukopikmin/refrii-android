package app.muko.mypantry.data.source.remote.services

import app.muko.mypantry.data.models.Box
import io.reactivex.Completable
import io.reactivex.Flowable
import okhttp3.RequestBody
import retrofit2.http.*

interface BoxService {
    @GET("/boxes")
    fun getAll(): Flowable<List<Box>>

    @GET("/boxes/{id}")
    fun get(@Path("id") id: Int): Flowable<Box>

    @POST("/boxes")
    fun create(@Body body: RequestBody): Completable

    @PUT("/boxes/{id}")
    fun update(
            @Path("id") id: Int,
            @Body body: RequestBody): Completable

//    @POST("/boxes/{id}/invitations")
//    fun invite(
//            @Path("id") id: Int,
//            @Body body: RequestBody): Flowable<Invitation>
//
//    @GET("/boxes/{id}/foods")
//    fun getFoodsInBox(@Path("id") id: Int): Flowable<List<Food>>
//
//    @GET("/boxes/{id}/units")
//    fun getUnitsForBox(@Path("id") id: Int): Flowable<List<Unit>>

    @DELETE("/boxes/{id}")
    fun remove(@Path("id") id: Int): Completable
}
