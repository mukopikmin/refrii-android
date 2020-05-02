package app.muko.mypantry.data.source.remote.services

import app.muko.mypantry.data.models.Food
import app.muko.mypantry.data.models.ShopPlan
import io.reactivex.Flowable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface FoodService {

    @GET("/foods")
    fun getAll(): Flowable<List<Food>>

    @GET("/foods/{id}")
    fun getById(@Path("id") id: Int): Flowable<Food>

    @GET("/foods/{id}/shop_plans")
    fun getShopPlans(@Path("id") id: Int): Flowable<List<ShopPlan>>

    @POST("/foods")
    fun create(@Body body: RequestBody): Flowable<Food>

    @JvmSuppressWildcards
    @Multipart
    @PUT("/foods/{id}")
    fun update(
            @Path("id") id: Int,
            @PartMap params: Map<String, RequestBody>,
            @Part files: MultipartBody.Part?
    ): Flowable<Food>

    @DELETE("/foods/{id}")
    fun remove(@Path("id") id: Int): Flowable<Void>

    @POST("/foods/{id}/notices")
    fun addNotice(@Path("id") id: Int, @Body body: RequestBody): Flowable<Food>
}
