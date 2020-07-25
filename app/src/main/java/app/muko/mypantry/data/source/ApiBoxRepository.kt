package app.muko.mypantry.data.source

import app.muko.mypantry.data.dao.LocalDatabase
import app.muko.mypantry.data.models.Box
import app.muko.mypantry.data.models.Food
import app.muko.mypantry.data.models.Invitation
import app.muko.mypantry.data.models.Unit
import app.muko.mypantry.data.source.remote.ApiRemoteBoxSource
import app.muko.mypantry.data.source.remote.services.BoxService
import io.reactivex.Flowable

class ApiBoxRepository(boxService: BoxService, val room: LocalDatabase) {

    private val mApiRemoteBoxSource = ApiRemoteBoxSource(boxService)
    private val mDao = room.boxDao()
    private val mFoodDao = room.foodDao()

    fun getBoxes(): Flowable<List<Box>> {
        mApiRemoteBoxSource.getBoxes()
                .flatMap { mDao.insertOrUpdate(it).andThen(Flowable.just(it)) }
                .subscribe()

        return Flowable.just(mDao.getAll())
    }

    fun getBox(id: Int): Flowable<Box> {
        return mApiRemoteBoxSource.getBox(id)
    }

    fun getFoods(id: Int): Flowable<List<Food>> {
        mApiRemoteBoxSource.getFoodsInBox(id)
                .flatMap { mFoodDao.insertOrUpdate(it).andThen(Flowable.just(it)) }
                .subscribe()

        return Flowable.just(mFoodDao.getAll().filter { it.box.id == id })
    }
//
//    fun getFoodsInBoxFromCache(id: Int): Flowable<List<Food>> {
//        return mApiLocalBoxSource.getFoodsInBox(id)
//    }
//
//    fun getBoxFromCache(id: Int): Flowable<Box?> {
//        return mApiLocalBoxSource.getBox(id)
//    }

    fun createBox(name: String, notice: String?): Flowable<Box> {
        return mApiRemoteBoxSource.createBox(name, notice)
    }

    fun getUnitsForBox(id: Int): Flowable<List<Unit>> {
        return mApiRemoteBoxSource.getUnitsForBox(id)
    }

//    fun getUnitsForBoxFromCache(id: Int): Flowable<List<Unit>> {
//        return mApiLocalBoxSource.getUnitsForBox(id)
//    }

    fun updateBox(id: Int, name: String?, notice: String?): Flowable<Box> {
        return mApiRemoteBoxSource.updateBox(id, name, notice)
    }

    fun removeBox(id: Int): Flowable<Void> {
        return mApiRemoteBoxSource.removeBox(id)
    }

    fun invite(boxId: Int, email: String): Flowable<Invitation> {
        return mApiRemoteBoxSource.invite(boxId, email)
    }
}