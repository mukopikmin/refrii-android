package app.muko.mypantry.data.source.data

import app.muko.mypantry.data.models.Notice
import io.reactivex.Completable

interface ApiNoticeDataSource {

    fun remove(notice: Notice): Completable
}