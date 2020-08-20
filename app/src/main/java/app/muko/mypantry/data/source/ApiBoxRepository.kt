package app.muko.mypantry.data.source

import app.muko.mypantry.data.dao.BoxDao
import app.muko.mypantry.data.models.Box
import app.muko.mypantry.data.source.data.ApiBoxDataSource
import app.muko.mypantry.data.source.local.ApiLocalBoxSource
import app.muko.mypantry.data.source.remote.ApiRemoteBoxSource
import app.muko.mypantry.data.source.remote.services.BoxService
import io.reactivex.Completable
import io.reactivex.CompletableObserver
import io.reactivex.Flowable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.rxkotlin.toFlowable

class ApiBoxRepository(service: BoxService, val dao: BoxDao) : ApiBoxDataSource {

    private val remote = ApiRemoteBoxSource(service)
    private val local = ApiLocalBoxSource(dao)

    override fun getAll(): Flowable<List<Box>> {
        Flowable.zip(
                remote.getAll(),
                local.getAll(),
                BiFunction<List<Box>, List<Box>, Pair<List<Box>, List<Box>>> { r, l -> Pair(r, l) }
        ).flatMap { pair ->
            pair.second.forEach { local.remove(it) }
            pair.first.map { local.create(it) }.toFlowable()
        }.subscribe()

        return local.getAll()
    }

    override fun get(id: Int): Flowable<Box?> {
        remote.get(id)
                .flatMap { local.create(it).toFlowable<Box?>() }
                .subscribe()

        return local.get(id)
    }

    override fun create(box: Box): Completable {
        remote.create(box)
                .subscribe()

        return local.create(box)
    }

    override fun update(box: Box): Completable {
        remote.update(box)
                .subscribe()

        return local.update(box)
    }

    override fun remove(box: Box): Completable {
        remote.remove(box)
                .subscribe(object : CompletableObserver {
                    override fun onComplete() {}
                    override fun onSubscribe(d: Disposable) {}
                    override fun onError(e: Throwable) {
                        create(box)
                    }
                })

        return local.remove(box)
    }
}