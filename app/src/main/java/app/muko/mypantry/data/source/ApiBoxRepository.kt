package app.muko.mypantry.data.source

import app.muko.mypantry.data.dao.LocalDatabase
import app.muko.mypantry.data.models.Box
import app.muko.mypantry.data.models.Food
import app.muko.mypantry.data.models.Invitation
import app.muko.mypantry.data.models.Unit
import app.muko.mypantry.data.source.remote.ApiRemoteBoxSource
import io.reactivex.Flowable
import retrofit2.Retrofit

class ApiBoxRepository(retrofit: Retrofit, val room: LocalDatabase) {

    private val mApiRemoteBoxSource = ApiRemoteBoxSource(retrofit)

    fun getBoxes(): Flowable<List<Box>> {
        val dao = room.boxDao()

        return mApiRemoteBoxSource.getBoxes()
//                .flatMap {
//                    dao.insertOrUpdate(it)
//                    Flowable.just(it)
//                }
//                .flatMap { boxes ->
//                    boxes.map { dao.insertOrUpdate(it).onCom }
//
//                    Flowable.just(boxes)
//                }
    }

//    fun getBoxesFromCache(): Flowable<List<Box>> {
//        return mApiLocalBoxSource.getBoxes()
//    }

    fun getBox(id: Int): Flowable<Box> {
        return mApiRemoteBoxSource.getBox(id)
    }

    fun getFoodsInBox(id: Int): Flowable<List<Food>> {
        return mApiRemoteBoxSource.getFoodsInBox(id)
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