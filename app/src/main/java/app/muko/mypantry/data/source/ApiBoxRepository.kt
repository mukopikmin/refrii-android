package app.muko.mypantry.data.source

import app.muko.mypantry.data.dao.BoxDao
import app.muko.mypantry.data.models.Box
import app.muko.mypantry.data.source.data.ApiBoxDataSource
import app.muko.mypantry.data.source.local.ApiLocalBoxSource
import app.muko.mypantry.data.source.remote.ApiRemoteBoxSource
import app.muko.mypantry.data.source.remote.services.BoxService
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.rxkotlin.Flowables

class ApiBoxRepository(service: BoxService, val dao: BoxDao) : ApiBoxDataSource {

    private val remote = ApiRemoteBoxSource(service)
    private val local = ApiLocalBoxSource(dao)

    override fun getAll(): Flowable<List<Box>> {
        return Flowables.zip(
                remote.getAll(),
                local.getAll()
        ) { r, l -> Pair(r, l) }
                .flatMap { pair ->
                    val remoteBoxes = pair.first
                    val localBoxes = pair.second

                    localBoxes.forEach {
                        if (!remoteBoxes.contains(it)) {
                            local.remove(it)
                        }
                    }

                    remoteBoxes.forEach {
                        if (localBoxes.contains(it)) {
                            local.update(it)
                        } else {
                            local.create(it)
                        }
                    }

                    Flowable.just(remoteBoxes)
                }
    }

    override fun get(id: Int): Flowable<Box?> {
        return remote.get(id)
                .flatMap { local.create(it).toFlowable<Box?>() }
    }

    override fun create(box: Box): Completable {
        return remote.create(box)
    }

    override fun update(box: Box): Completable {
        return remote.update(box)
    }

    override fun remove(box: Box): Completable {
        return remote.remove(box)
    }
}