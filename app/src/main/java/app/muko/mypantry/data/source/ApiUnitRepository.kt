package app.muko.mypantry.data.source

import app.muko.mypantry.data.dao.UnitDao
import app.muko.mypantry.data.models.Box
import app.muko.mypantry.data.models.Unit
import app.muko.mypantry.data.source.data.ApiUnitDataSource
import app.muko.mypantry.data.source.local.ApiLocalUnitSource
import app.muko.mypantry.data.source.remote.ApiRemoteUnitSource
import app.muko.mypantry.data.source.remote.services.UnitService
import io.reactivex.Completable
import io.reactivex.Flowable

class ApiUnitRepository(
        service: UnitService,
        val dao: UnitDao
) : ApiUnitDataSource {

    private val remote = ApiRemoteUnitSource(service)

    //    val dao = room.unitDao()
    private val local = ApiLocalUnitSource(dao)

//    fun getUnits(userId: Int): Flowable<List<Unit>> {
//        return mApiRemoteUnitSource.getUnits()
//    }
//
//    fun getUnit(id: Int): Flowable<Unit> {
//        return mApiRemoteUnitSource.getUnit(id)
//    }
//
//    fun createUnit(label: String, step: Double): Flowable<Unit> {
//        return mApiRemoteUnitSource.createUnit(label, step)
//    }
//
//    fun updateUnit(id: Int, label: String?, step: Double?): Flowable<Unit> {
//        return mApiRemoteUnitSource.updateUnit(id, label, step)
//    }
//
//    fun removeUnit(id: Int): Flowable<Void> {
//        return mApiRemoteUnitSource.removeUnit(id)
//    }

    override fun getAll(): Flowable<List<Unit>> {
        local.getAll()
                .flatMap { units ->
                    units.map { local.remove(it) }

                    remote.getAll()
                }
                .flatMap { units ->
                    units.map { local.create(it) }

                    Flowable.just(units)
                }
                .subscribe()

        return local.getAll()
    }

    override fun getByBox(box: Box): Flowable<List<Unit>> {
        local.getByBox(box)
                .flatMap { units ->
                    units.map { local.remove(it) }

                    remote.getByBox(box)
                }
                .flatMap { units ->
                    Flowable.just(units.map { local.create(it) })
                }
                .subscribe()

        return local.getByBox(box)
    }

    override fun get(id: Int): Flowable<Unit?> {
        remote.get(id)
                .flatMap {
                    local.create(it)
                    Flowable.just(it)
                }
                .subscribe()

        return local.get(id)
    }

    override fun create(unit: Unit): Completable {
        remote.create(unit)
                .subscribe()

        return local.create(unit)
    }

    override fun update(unit: Unit): Completable {
        remote.update(unit)
                .subscribe()

        return local.update(unit)
    }

    override fun remove(unit: Unit): Completable {
        remote.remove(unit)
                .subscribe()

        return local.remove(unit)
    }
}