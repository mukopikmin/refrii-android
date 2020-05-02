package app.muko.mypantry.data.source

import app.muko.mypantry.data.source.remote.ApiRemoteNoticeSource
import io.reactivex.Flowable
import retrofit2.Retrofit

class ApiNoticeRepository(retrofit: Retrofit) {

    private val mApiRemoteNoticeSource = ApiRemoteNoticeSource(retrofit)

    fun remove(id: Int): Flowable<Void> {
        return mApiRemoteNoticeSource.remove(id)
    }
}