package app.muko.mypantry.data.source.remote.services

import app.muko.mypantry.data.models.ShopPlan
import io.reactivex.Flowable
import okhttp3.RequestBody
import retrofit2.http.*

interface ShopPlanService {
    @GET("/shop_plans")
    fun getShopPlans(): Flowable<List<ShopPlan>>

    @POST("/shop_plans")
    fun createShopPlans(@Body body: RequestBody): Flowable<ShopPlan>

    @PUT("/shop_plans/{id}")
    fun updateShopPlan(
            @Path("id") id: Int,
            @Body body: RequestBody
    ): Flowable<ShopPlan>
}
