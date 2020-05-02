package app.muko.mypantry.data.source.remote.services

import io.reactivex.Flowable
import retrofit2.http.DELETE
import retrofit2.http.Path

interface NoticeService {

    @DELETE("/notices/{id}")
    fun remove(@Path("id") id: Int): Flowable<Void>
}