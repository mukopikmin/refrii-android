package app.muko.mypantry.data.source.remote.services

import app.muko.mypantry.data.models.Box
import app.muko.mypantry.data.models.Food
import app.muko.mypantry.data.models.Invitation
import app.muko.mypantry.data.models.Unit
import okhttp3.RequestBody
import retrofit2.http.*
import rx.Observable

interface BoxService {
    @GET("/boxes")
    fun getBoxes(): Observable<List<Box>>

    @GET("/boxes/{id}")
    fun getBox(@Path("id") id: Int): Observable<Box>

    @POST("/boxes")
    fun createBox(@Body body: RequestBody): Observable<Box>

    @PUT("/boxes/{id}")
    fun updateBox(
            @Path("id") id: Int,
            @Body body: RequestBody): Observable<Box>

    @POST("/boxes/{id}/invitations")
    fun invite(
            @Path("id") id: Int,
            @Body body: RequestBody): Observable<Invitation>

    @GET("/boxes/{id}/foods")
    fun getFoodsInBox(@Path("id") id: Int): Observable<List<Food>>

    @GET("/boxes/{id}/units")
    fun getUnitsForBox(@Path("id") id: Int): Observable<List<Unit>>

    @DELETE("/boxes/{id}")
    fun removeBox(@Path("id") id: Int): Observable<Void>
}
