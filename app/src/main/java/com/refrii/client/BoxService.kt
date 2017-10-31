package com.refrii.client

import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PUT
import retrofit2.http.Path
import rx.Observable

interface BoxService {
    @GET("/boxes")
    fun getBoxes(): Observable<List<Box>>

    @GET("/boxes/{id}")
    fun getBox(@Path("id") id: Int): Call<Box>

    @PUT("/boxes/{id}")
    fun updateBox(
            @Path("id") id: Int,
            @Body body: RequestBody): Call<Box>
}
