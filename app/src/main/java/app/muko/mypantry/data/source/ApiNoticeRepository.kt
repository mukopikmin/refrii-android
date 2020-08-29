package app.muko.mypantry.data.source

import app.muko.mypantry.data.models.Notice
import app.muko.mypantry.data.source.data.ApiNoticeDataSource
import app.muko.mypantry.data.source.remote.ApiRemoteNoticeSource
import app.muko.mypantry.data.source.remote.services.NoticeService
import io.reactivex.Completable

class ApiNoticeRepository(service: NoticeService) : ApiNoticeDataSource {

    private val remote = ApiRemoteNoticeSource(service)

    override fun remove(notice: Notice): Completable {
        return remote.remove(notice)
    }
}