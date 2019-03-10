package com.refrii.client.data.api.source.remote.services

import com.refrii.client.data.api.models.User
import retrofit2.http.GET
import rx.Observable

interface UserService {
    @GET("/users/verify")
    fun verify(): Observable<User>
}
