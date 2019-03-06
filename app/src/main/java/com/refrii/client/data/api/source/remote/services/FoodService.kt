package com.refrii.client.data.api.source.remote.services

import com.refrii.client.data.api.models.Food
import okhttp3.RequestBody
import retrofit2.http.*
import rx.Observable

interface FoodService {
    @GET("/foods")
    fun getFodos(): Observable<List<Food>>

    @GET("/foods/{id}")
    fun getFood(@Path("id") id: Int): Observable<Food>

    @POST("/foods")
    fun addFood(@Body body: RequestBody): Observable<Food>

    @PUT("/foods/{id}")
    fun updateFood(
            @Path("id") id: Int,
            @Body body: RequestBody): Observable<Food>

    @DELETE("/foods/{id}")
    fun removeFood(@Path("id") id: Int): Observable<Void>
}
