package com.refrii.client.services

import com.refrii.client.models.Unit
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*
import rx.Observable

interface UnitService {
    @GET("/units")
    fun getUnits(): Observable<MutableList<Unit>>

    @POST("/units")
    fun createUnit(@Body body: RequestBody): Observable<Unit>

    @PUT("/units/{id}")
    fun updateUnit(
            @Path("id") id: Int,
            @Body body: RequestBody): Observable<Unit>
}
