package app.muko.mypantry.data.source.remote.services

import app.muko.mypantry.data.models.Box
import app.muko.mypantry.data.models.Food
import app.muko.mypantry.data.models.Invitation
import app.muko.mypantry.data.models.Unit
import io.reactivex.Flowable
import okhttp3.RequestBody
import retrofit2.http.*

interface BoxService {
    @GET("/boxes")
    fun getBoxes(): Flowable<List<Box>>

    @GET("/boxes/{id}")
    fun getBox(@Path("id") id: Int): Flowable<Box>

    @POST("/boxes")
    fun createBox(@Body body: RequestBody): Flowable<Box>

    @PUT("/boxes/{id}")
    fun updateBox(
            @Path("id") id: Int,
            @Body body: RequestBody): Flowable<Box>

    @POST("/boxes/{id}/invitations")
    fun invite(
            @Path("id") id: Int,
            @Body body: RequestBody): Flowable<Invitation>

    @GET("/boxes/{id}/foods")
    fun getFoodsInBox(@Path("id") id: Int): Flowable<List<Food>>

    @GET("/boxes/{id}/units")
    fun getUnitsForBox(@Path("id") id: Int): Flowable<List<Unit>>

    @DELETE("/boxes/{id}")
    fun removeBox(@Path("id") id: Int): Flowable<Void>
}
