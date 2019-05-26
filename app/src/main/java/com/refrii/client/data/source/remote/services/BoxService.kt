package com.refrii.client.data.source.remote.services

import com.refrii.client.data.models.Box
import com.refrii.client.data.models.Food
import com.refrii.client.data.models.Invitation
import com.refrii.client.data.models.Unit
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

    @POST("/boxes/{id}/invite")
    fun invite(
            @Path("id") id: Int,
            @Body body: RequestBody): Observable<Invitation>

    @DELETE("/boxes/{id}/invite")
    fun uninvite(
            @Path("id") id: Int,
            @Query("email") email: String): Observable<Void>

    @GET("/boxes/{id}/foods")
    fun getFoodsInBox(@Path("id") id: Int): Observable<List<Food>>

    @GET("/boxes/{id}/units")
    fun getUnitsForBox(@Path("id") id: Int): Observable<List<Unit>>

    @DELETE("/boxes/{id}")
    fun removeBox(@Path("id") id: Int): Observable<Void>
}