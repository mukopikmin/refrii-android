package app.muko.mypantry.data.source.remote.services

import app.muko.mypantry.data.models.User
import okhttp3.RequestBody
import retrofit2.http.*
import rx.Observable

interface UserService {
    @POST("/users/google")
    fun signupWithGoogle(): Observable<User>

    @GET("/users/verify")
    fun verify(): Observable<User>

    @POST("/users/{id}/push_tokens")
    fun registerPushToken(@Path("id") id: Int, @Body body: RequestBody): Observable<User>

    @PUT("/users/{id}")
    fun update(@Path("id") id: Int, @Body body: RequestBody): Observable<User>
}
