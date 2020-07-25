package app.muko.mypantry.data.source.local

import app.muko.mypantry.data.dao.LocalDatabase
import app.muko.mypantry.data.models.Box
import app.muko.mypantry.data.source.ApiFoodDataSource
import io.reactivex.Flowable

class ApiLocalFoodSource(room: LocalDatabase) : ApiFoodDataSource {

    private val mFoodDao = room.foodDao()

    override fun getByBox(boxId: Int): Flowable<List<Box>> {
        TODO("Not yet implemented")
    }

    override fun get(id: Int): Flowable<Box?> {
        TODO("Not yet implemented")
    }

    override fun create(name: String, notice: String?): Flowable<Box> {
        TODO("Not yet implemented")
    }

    override fun update(id: Int, name: String?, notice: String?): Flowable<Box> {
        TODO("Not yet implemented")
    }

    override fun remove(id: Int): Flowable<Void> {
        TODO("Not yet implemented")
    }

}