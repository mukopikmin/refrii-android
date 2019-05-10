package com.refrii.client.data.source.remote.services

import com.refrii.client.data.models.Food
import com.refrii.client.data.models.ShopPlan
import okhttp3.RequestBody
import retrofit2.http.*
import rx.Observable

interface FoodService {
    @GET("/foods")
    fun getFodos(): Observable<List<Food>>

    @GET("/foods/{id}")
    fun getFood(@Path("id") id: Int): Observable<Food>

    @GET("/foods/{id}/shop_plans")
    fun getShopPlans(@Path("id") id: Int): Observable<List<ShopPlan>>

    @POST("/foods")
    fun addFood(@Body body: RequestBody): Observable<Food>

    @PUT("/foods/{id}")
    fun updateFood(
            @Path("id") id: Int,
            @Body body: RequestBody): Observable<Food>

    @DELETE("/foods/{id}")
    fun removeFood(@Path("id") id: Int): Observable<Void>
}
