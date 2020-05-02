package app.muko.mypantry.data.source

import app.muko.mypantry.data.models.Box
import app.muko.mypantry.data.models.Food
import app.muko.mypantry.data.models.Invitation
import app.muko.mypantry.data.models.Unit
import io.reactivex.Flowable

interface ApiBoxDataSource {

    fun getBoxes(): Flowable<List<Box>>
    fun getBoxesFromCache(): Flowable<List<Box>>
    fun getBox(id: Int): Flowable<Box?>
    fun getFoodsInBox(id: Int): Flowable<List<Food>>
    fun getFoodsInBoxFromCache(id: Int): Flowable<List<Food>>
    fun getBoxFromCache(id: Int): Flowable<Box?>
    fun createBox(name: String, notice: String?): Flowable<Box>
    fun getUnitsForBox(id: Int): Flowable<List<Unit>>
    fun getUnitsForBoxFromCache(id: Int): Flowable<List<Unit>>
    fun updateBox(id: Int, name: String?, notice: String?): Flowable<Box>
    fun removeBox(id: Int): Flowable<Void>
    fun invite(boxId: Int, email: String): Flowable<Invitation>
    fun uninvite(boxId: Int, email: String): Flowable<Void>
}