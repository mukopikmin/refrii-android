package app.muko.mypantry.data.source.local

import app.muko.mypantry.data.dao.LocalDatabase
import app.muko.mypantry.data.models.Box
import app.muko.mypantry.data.models.Food
import app.muko.mypantry.data.models.Invitation
import app.muko.mypantry.data.models.Unit
import app.muko.mypantry.data.source.ApiBoxDataSource
import io.reactivex.Completable
import io.reactivex.Flowable

class ApiLocalBoxSource(room: LocalDatabase) : ApiBoxDataSource {

    private val mBoxDao = room.boxDao()
    private val foodDao = room.foodDao()

    override fun getAll(): Flowable<List<Box>> {
        return Flowable.just(mBoxDao.getAll())
    }

    override fun get(id: Int): Flowable<Box?> {
        return Flowable.just(mBoxDao.get(id))
    }

    override fun getFoods(id: Int): Flowable<List<Food>> {
        return Flowable.just(foodDao.getAll().filter { it.box.id == id })
    }

    override fun create(box: Box): Flowable<Box> {
        return mBoxDao.insertOrUpdate(box)
                .andThen(Flowable.just(box))
    }

    override fun getUnits(id: Int): Flowable<List<Unit>> {
        TODO("Not yet implemented")
    }

    override fun update(box: Box): Completable {
        return mBoxDao.insertOrUpdate(box)
    }

    override fun remove(box: Box): Completable {
        return mBoxDao.delete(box)
    }

    override fun invite(boxId: Int, email: String): Flowable<Invitation> {
        TODO("Not yet implemented")
    }
}