package com.refrii.client.services

import com.refrii.client.models.Food
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import rx.Observable

interface FoodService {
    @GET("/foods/{id}")
    fun getFood(@Path("id") id: Int): Observable<Food>

    @POST("/foods")
    fun addFood(@Body body: RequestBody): Observable<Food>

    @PUT("/foods/{id}")
    fun updateFood(
            @Path("id") id: Int,
            @Body body: RequestBody): Call<Food>

    @DELETE("/foods/{id}")
    fun remove(@Path("id") id: Int): Call<Void>
}
