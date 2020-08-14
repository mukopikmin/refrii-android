package app.muko.mypantry.data.source.remote.services

import app.muko.mypantry.data.models.ShopPlan
import io.reactivex.Completable
import io.reactivex.Flowable
import okhttp3.RequestBody
import retrofit2.http.*

interface ShopPlanService {

    @GET("/shop_plans")
    fun getAll(): Flowable<List<ShopPlan>>

    @GET("/foods/{foodId}/shop_plans")
    fun getByFood(@Path("foodId") foodId: Int): Flowable<List<ShopPlan>>

    @GET("/shop_plans/{id}")
    fun get(@Path("id") id: Int): Flowable<ShopPlan>

    @POST("/shop_plans")
    fun create(@Body body: RequestBody): Completable

    @PUT("/shop_plans/{id}")
    fun update(
            @Path("id") id: Int,
            @Body body: RequestBody
    ): Completable

    @DELETE("/shop_plans/{id}")
    fun remove(@Path("id") id: Int): Completable
}
