package app.muko.mypantry.data.source.remote.services

import app.muko.mypantry.data.models.Unit
import io.reactivex.Flowable
import okhttp3.RequestBody
import retrofit2.http.*

interface UnitService {
    @GET("/units")
    fun getUnits(): Flowable<List<Unit>>

    @GET("/units/{id}")
    fun getUnit(@Path("id") id: Int): Flowable<Unit>

    @POST("/units")
    fun createUnit(@Body body: RequestBody): Flowable<Unit>

    @PUT("/units/{id}")
    fun updateUnit(
            @Path("id") id: Int,
            @Body body: RequestBody): Flowable<Unit>

    @DELETE("/units/{id}")
    fun deleteUnit(@Path("id") id: Int): Flowable<Void>
}
