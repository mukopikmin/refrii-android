package com.refrii.client

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.QueryMap

interface AuthService {
    @GET("/auth/google/token")
    fun getToken(@QueryMap params: Map<String, String>): Call<Credential>
}
