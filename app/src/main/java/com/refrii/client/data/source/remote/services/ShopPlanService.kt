package com.refrii.client.data.source.remote.services

import com.refrii.client.data.models.ShopPlan
import okhttp3.RequestBody
import retrofit2.http.*
import rx.Observable

interface ShopPlanService {
    @GET("/shop_plans")
    fun getShopPlans(): Observable<List<ShopPlan>>

    @POST("/shop_plans")
    fun createShopPlans(@Body body: RequestBody): Observable<ShopPlan>

    @PUT("/shop_plans/{id}/complete")
    fun updateShopPlan(@Path("id") id: Int): Observable<ShopPlan>
}
