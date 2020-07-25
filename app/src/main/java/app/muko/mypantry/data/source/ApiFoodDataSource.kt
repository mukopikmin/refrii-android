package app.muko.mypantry.data.source

import app.muko.mypantry.data.models.Box
import io.reactivex.Flowable

interface ApiFoodDataSource {

    fun getByBox(boxId: Int): Flowable<List<Box>>
    fun get(id: Int): Flowable<Box?>
    fun create(name: String, notice: String?): Flowable<Box>
    fun update(id: Int, name: String?, notice: String?): Flowable<Box>
    fun remove(id: Int): Flowable<Void>
}