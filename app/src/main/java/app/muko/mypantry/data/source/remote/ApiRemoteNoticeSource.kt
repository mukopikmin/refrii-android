package app.muko.mypantry.data.source.remote

import app.muko.mypantry.data.models.Notice
import app.muko.mypantry.data.source.data.ApiNoticeDataSource
import app.muko.mypantry.data.source.remote.services.NoticeService
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class ApiRemoteNoticeSource(
        private val service: NoticeService
) : ApiNoticeDataSource {

    override fun remove(notice: Notice): Completable {
        return service.remove(notice.id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }
}