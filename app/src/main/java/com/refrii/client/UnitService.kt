package com.refrii.client

import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface UnitService {
    @get:GET("/units")
    val units: Call<MutableList<Unit>>

    @POST("/units")
    fun createUnit(@Body body: RequestBody): Call<Unit>

    @PUT("/units/{id}")
    fun updateUnit(
            @Path("id") id: Int,
            @Body body: RequestBody): Call<Unit>
}
