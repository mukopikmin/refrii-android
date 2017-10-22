package com.refrii.client

import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PUT
import retrofit2.http.Path

interface UnitService {
    @get:GET("/units")
    val units: Call<List<Unit>>

    @PUT("/units/{id}")
    fun updateUnit(
            @Path("id") id: Int,
            @Body body: RequestBody): Call<Unit>
}
