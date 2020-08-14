package app.muko.mypantry.data.source.local

import app.muko.mypantry.data.dao.UnitDao
import app.muko.mypantry.data.models.Box
import app.muko.mypantry.data.models.Unit
import app.muko.mypantry.data.source.data.ApiUnitDataSource
import io.reactivex.Completable
import io.reactivex.Flowable

class ApiLocalUnitSource(
        private val unitDao: UnitDao
) : ApiUnitDataSource {

    override fun getAll(): Flowable<List<Unit>> {
        return Flowable.just(unitDao.getAll())
    }

    override fun getByBox(box: Box): Flowable<List<Unit>> {
        val units = unitDao.getAll()
                .filter { it.user?.id == box.owner?.id }

        return Flowable.just(units)
    }

    override fun get(id: Int): Flowable<Unit?> {
        return Flowable.just(unitDao.get(id))
    }

    override fun create(unit: Unit): Completable {
        unitDao.insertOrUpdate(unit)

        return Completable.complete()
    }

    override fun update(unit: Unit): Completable {
        unitDao.insertOrUpdate(unit)

        return Completable.complete()
    }

    override fun remove(unit: Unit): Completable {
        unitDao.delete(unit)

        return Completable.complete()
    }
}