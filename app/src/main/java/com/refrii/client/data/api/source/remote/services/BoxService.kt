package com.refrii.client.data.api.source.remote.services

import com.refrii.client.data.api.models.Box
import com.refrii.client.data.api.models.Food
import com.refrii.client.data.api.models.Unit
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
import rx.Observable

interface BoxService {
    @GET("/boxes")
    fun getBoxes(): Observable<List<Box>>

    @GET("/boxes/{id}")
    fun getBox(@Path("id") id: Int): Observable<Box>

    @PUT("/boxes/{id}")
    fun updateBox(
            @Path("id") id: Int,
            @Body body: RequestBody): Observable<Box>

    @GET("/boxes/{id}/foods")
    fun getFoodsInBox(@Path("id") id: Int): Observable<List<Food>>

    @GET("/boxes/{id}/units")
    fun getUnitsForBox(@Path("id") id: Int): Observable<List<Unit>>
}
