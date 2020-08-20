package app.muko.mypantry.data.source

import app.muko.mypantry.data.dao.UnitDao
import app.muko.mypantry.data.models.Box
import app.muko.mypantry.data.models.Unit
import app.muko.mypantry.data.source.data.ApiUnitDataSource
import app.muko.mypantry.data.source.local.ApiLocalUnitSource
import app.muko.mypantry.data.source.remote.ApiRemoteUnitSource
import app.muko.mypantry.data.source.remote.services.UnitService
import io.reactivex.Completable
import io.reactivex.CompletableObserver
import io.reactivex.Flowable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.rxkotlin.toFlowable

class ApiUnitRepository(
        service: UnitService,
        val dao: UnitDao
) : ApiUnitDataSource {

    private val remote = ApiRemoteUnitSource(service)
    private val local = ApiLocalUnitSource(dao)

    override fun getAll(): Flowable<List<Unit>> {
        Flowable.zip(
                remote.getAll(),
                local.getAll(),
                BiFunction<List<Unit>, List<Unit>, Pair<List<Unit>, List<Unit>>> { r, l -> Pair(r, l) }
        ).flatMap { pair ->
            pair.second.forEach { local.remove(it) }
            pair.first.map { local.create(it) }.toFlowable()
        }.subscribe()

        return local.getAll()
    }

    override fun getByBox(box: Box): Flowable<List<Unit>> {
        Flowable.zip(
                remote.getByBox(box),
                local.getByBox(box),
                BiFunction<List<Unit>, List<Unit>, Pair<List<Unit>, List<Unit>>> { r, l -> Pair(r, l) }
        ).flatMap { pair ->
            pair.second.forEach { local.remove(it) }
            pair.first.map { local.create(it) }.toFlowable()
        }.subscribe()

        return local.getByBox(box)
    }

    override fun get(id: Int): Flowable<Unit?> {
        remote.get(id)
                .flatMap { local.create(it).toFlowable<Unit?>() }
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
                .subscribe(object : CompletableObserver {
                    override fun onComplete() {}
                    override fun onSubscribe(d: Disposable) {}
                    override fun onError(e: Throwable) {
                        local.create(unit)
                    }
                })

        return local.remove(unit)
    }
}