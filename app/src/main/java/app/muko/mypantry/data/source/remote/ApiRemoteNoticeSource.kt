package app.muko.mypantry.data.source.remote

import app.muko.mypantry.data.source.remote.services.NoticeService
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit

class ApiRemoteNoticeSource(private val mRetrofit: Retrofit) {

    fun remove(id: Int): Flowable<Void> {
        return mRetrofit.create(NoticeService::class.java)
                .remove(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }
}