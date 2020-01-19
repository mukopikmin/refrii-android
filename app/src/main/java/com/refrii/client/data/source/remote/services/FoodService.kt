package com.refrii.client.data.source.remote.services

import com.refrii.client.data.models.Food
import com.refrii.client.data.models.ShopPlan
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*
import rx.Observable

interface FoodService {

    @GET("/foods")
    fun getAll(): Observable<List<Food>>

    @GET("/foods/{id}")
    fun getById(@Path("id") id: Int): Observable<Food>

    @GET("/foods/{id}/shop_plans")
    fun getShopPlans(@Path("id") id: Int): Observable<List<ShopPlan>>

    @POST("/foods")
    fun create(@Body body: RequestBody): Observable<Food>

    @JvmSuppressWildcards
    @Multipart
    @PUT("/foods/{id}")
    fun update(
            @Path("id") id: Int,
            @PartMap params: Map<String, RequestBody>,
            @Part files: MultipartBody.Part?
    ): Observable<Food>

    @DELETE("/foods/{id}")
    fun remove(@Path("id") id: Int): Observable<Void>

    @POST("/foods/{id}/notices")
    fun addNotice(@Path("id") id: Int, @Body body: RequestBody): Observable<Food>
}
