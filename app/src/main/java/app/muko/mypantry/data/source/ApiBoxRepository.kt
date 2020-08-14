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

class ApiBoxRepository(service: BoxService, dao: BoxDao) : ApiBoxDataSource {

    private val remote = ApiRemoteBoxSource(service)
    private val local = ApiLocalBoxSource(dao)

//    fun getBoxes(): Flowable<List<Box>> {
//        remote.getBoxes()
//                .flatMap { mDao.insertOrUpdate(it).andThen(Flowable.just(it)) }
//                .subscribe()
//
//        return Flowable.just(mDao.getAll())
//    }
//
//    fun getBox(id: Int): Flowable<Box> {
//        return remote.getBox(id)
//    }
//
//    fun getFoods(id: Int): Flowable<List<Food>> {
//        remote.getFoodsInBox(id)
//                .flatMap { mFoodDao.insertOrUpdate(it).andThen(Flowable.just(it)) }
//                .subscribe()
//
//        return Flowable.just(mFoodDao.getAll().filter { it.box.id == id })
//    }
////
////    fun getFoodsInBoxFromCache(id: Int): Flowable<List<Food>> {
////        return mApiLocalBoxSource.getFoodsInBox(id)
////    }
////
////    fun getBoxFromCache(id: Int): Flowable<Box?> {
////        return mApiLocalBoxSource.getBox(id)
////    }
//
//    fun createBox(name: String, notice: String?): Flowable<Box> {
//        return remote.createBox(name, notice)
//    }
//
//    fun getUnitsForBox(id: Int): Flowable<List<Unit>> {
//        return remote.getUnitsForBox(id)
//    }
//
////    fun getUnitsForBoxFromCache(id: Int): Flowable<List<Unit>> {
////        return mApiLocalBoxSource.getUnitsForBox(id)
////    }
//
//    fun updateBox(id: Int, name: String?, notice: String?): Flowable<Box> {
//        return remote.updateBox(id, name, notice)
//    }
//
//    fun removeBox(id: Int): Flowable<Void> {
//        return remote.removeBox(id)
//    }
//
//    fun invite(boxId: Int, email: String): Flowable<Invitation> {
//        return remote.invite(boxId, email)
//    }


    override fun getAll(): Flowable<List<Box>> {
        local.getAll()
                .flatMap { boxes ->
                    boxes.map { local.remove(it) }

                    remote.getAll()
                }
                .flatMap { boxes ->
                    boxes.map { local.create(it) }

                    Flowable.just(boxes)
                }
                .subscribe()

        return local.getAll()
    }

    override fun get(id: Int): Flowable<Box?> {
        remote.get(id)
                .flatMap {
                    local.create(it)

                    Flowable.just(it)
                }
                .subscribe()

        return local.get(id)
    }

    override fun create(box: Box): Completable {
        remote.create(box)
                .andThen { local.create(box) }
                .subscribe()

        return local.create(box)
    }

    override fun update(box: Box): Completable {
        remote.update(box)
                .andThen { local.update(box) }
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