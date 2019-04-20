package com.refrii.client.data.source.remote.services

import com.refrii.client.data.models.Unit
import okhttp3.RequestBody
import retrofit2.http.*
import rx.Observable

interface UnitService {
    @GET("/units")
    fun getUnits(): Observable<List<Unit>>

    @GET("/units/{id}")
    fun getUnit(@Path("id") id: Int): Observable<Unit>

    @POST("/units")
    fun createUnit(@Body body: RequestBody): Observable<Unit>

    @PUT("/units/{id}")
    fun updateUnit(
            @Path("id") id: Int,
            @Body body: RequestBody): Observable<Unit>

    @DELETE("/units/{id}")
    fun deleteUnit(@Path("id") id: Int): Observable<Void>
}
