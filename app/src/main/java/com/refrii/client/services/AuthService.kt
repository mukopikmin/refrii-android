package com.refrii.client.services

import com.refrii.client.models.Credential
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface AuthService {
    @GET("/auth/google/token")
    fun getToken(@QueryMap params: Map<String, String>): Call<Credential>
}
