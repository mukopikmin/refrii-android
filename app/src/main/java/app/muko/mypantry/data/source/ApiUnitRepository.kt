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
import io.reactivex.rxkotlin.Flowables

class ApiUnitRepository(
        service: UnitService,
        val dao: UnitDao
) : ApiUnitDataSource {

    private val remote = ApiRemoteUnitSource(service)
    private val local = ApiLocalUnitSource(dao)

    override fun getAll(): Flowable<List<Unit>> {
        return Flowables.zip(
                remote.getAll(),
                local.getAll()
        ) { r, l -> Pair(r, l) }
                .flatMap { pair ->
                    val remoteUnits = pair.first
                    val localUnits = pair.second

                    localUnits.forEach {
                        if (!remoteUnits.contains(it)) {
                            local.remove(it)
                        }
                    }

                    remoteUnits.forEach {
                        if (localUnits.contains(it)) {
                            local.update(it)
                        } else {
                            local.create((it))
                        }
                    }

                    Flowable.just(remoteUnits)
                }
    }

    override fun getByBox(box: Box): Flowable<List<Unit>> {
        return Flowable.zip(
                remote.getByBox(box),
                local.getByBox(box),
                { r, l -> Pair(r, l) }
        ).flatMap { pair ->
            pair.second.forEach { local.remove(it) }
            pair.first.map { local.create(it) }

            Flowable.just(pair.first)
        }
    }

    override fun get(id: Int): Flowable<Unit?> {
        return remote.get(id)
                .flatMap { local.create(it).toFlowable<Unit?>() }
    }

    override fun create(unit: Unit): Completable {
        return remote.create(unit)
    }

    override fun update(unit: Unit): Completable {
        return remote.update(unit)
    }

    override fun remove(unit: Unit): Completable {
        return remote.remove(unit)
    }
}