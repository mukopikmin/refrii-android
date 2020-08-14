package app.muko.mypantry.data.source.local

import app.muko.mypantry.data.dao.BoxDao
import app.muko.mypantry.data.models.Box
import app.muko.mypantry.data.source.data.ApiBoxDataSource
import io.reactivex.Completable
import io.reactivex.Flowable

class ApiLocalBoxSource(
        private val dao: BoxDao
) : ApiBoxDataSource {

    override fun getAll(): Flowable<List<Box>> {
        return Flowable.just(dao.getAll())
    }

    override fun get(id: Int): Flowable<Box?> {
        return Flowable.just(dao.get(id))
    }

    override fun create(box: Box): Completable {
        dao.insertOrUpdate(box)

        return Completable.complete()
    }

    override fun update(box: Box): Completable {
        dao.insertOrUpdate(box)

        return Completable.complete()
    }

    override fun remove(box: Box): Completable {
        dao.delete(box)

        return Completable.complete()
    }
}