package app.muko.mypantry.data.source.remote.services

import app.muko.mypantry.data.models.User
import io.reactivex.Flowable
import okhttp3.RequestBody
import retrofit2.http.*

interface UserService {
    @POST("/users/google")
    fun signupWithGoogle(): Flowable<User>

    @GET("/users/verify")
    fun verify(): Flowable<User>

    @POST("/users/{id}/push_tokens")
    fun registerPushToken(@Path("id") id: Int, @Body body: RequestBody): Flowable<User>

    @PUT("/users/{id}")
    fun update(@Path("id") id: Int, @Body body: RequestBody): Flowable<User>
}
