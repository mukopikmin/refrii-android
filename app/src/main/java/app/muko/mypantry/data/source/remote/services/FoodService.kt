package app.muko.mypantry.data.source.remote.services

import app.muko.mypantry.data.models.Food
import io.reactivex.Completable
import io.reactivex.Flowable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface FoodService {

    @GET("/foods")
    fun getAll(): Flowable<List<Food>>

    @GET("/foods/{id}")
    fun get(@Path("id") id: Int): Flowable<Food>

    @GET("/boxes/{boxId}/foods")
    fun getByBox(@Path("boxId") boxId: Int): Flowable<List<Food>>

    @POST("/foods")
    fun create(@Body body: RequestBody): Completable

    @JvmSuppressWildcards
    @Multipart
    @PUT("/foods/{id}")
    fun update(
            @Path("id") id: Int,
            @PartMap params: Map<String, RequestBody>,
            @Part files: MultipartBody.Part?
    ): Completable

    @DELETE("/foods/{id}")
    fun remove(@Path("id") id: Int): Completable

    @POST("/foods/{id}/notices")
    fun create(
            @Path("id") id: Int,
            @Body body: RequestBody
    ): Flowable<Food>

}
