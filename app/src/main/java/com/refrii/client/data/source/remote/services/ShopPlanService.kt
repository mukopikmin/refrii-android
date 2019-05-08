package com.refrii.client.data.source.remote.services

import com.refrii.client.data.models.ShopPlan
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import rx.Observable

interface ShopPlanService {
    @GET("/shop_plans")
    fun getShopPlans(): Observable<List<ShopPlan>>

    @POST("/shop_plans")
    fun createShopPlans(@Body body: RequestBody): Observable<ShopPlan>
}
