package com.refrii.client

import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.FieldMap
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.PartMap
import retrofit2.http.Path

interface FoodService {

    @GET("/foods/{id}")
    fun getFood(@Path("id") id: Int): Call<Food>

    @POST("/foods")
    fun addFood(@Body body: RequestBody): Call<Food>

    @PUT("/foods/{id}")
    fun updateFood(
            @Path("id") id: Int,
            @Body body: RequestBody): Call<Food>

    @DELETE("/foods/{id}")
    fun remove(@Path("id") id: Int): Call<Void>
}
