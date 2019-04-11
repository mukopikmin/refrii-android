package com.refrii.client.data.api.source.remote.services

import com.refrii.client.data.api.models.User
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import rx.Observable

interface UserService {
    @GET("/users/verify")
    fun verify(): Observable<User>

    @POST("users/{id}/push_token")
    fun registerPushToken(@Path("id") id: Int,
                          @Body body: RequestBody): Observable<User>
}
