package app.muko.mypantry.data.source

import app.muko.mypantry.data.models.Box
import app.muko.mypantry.data.models.Food
import app.muko.mypantry.data.models.Invitation
import app.muko.mypantry.data.models.Unit
import io.reactivex.Completable
import io.reactivex.Flowable

interface ApiBoxDataSource {

    fun getAll(): Flowable<List<Box>>
    fun get(id: Int): Flowable<Box?>
    fun getFoods(id: Int): Flowable<List<Food>>
    fun create(box: Box): Flowable<Box>
    fun getUnits(id: Int): Flowable<List<Unit>>
    fun update(box: Box): Completable
    fun remove(box: Box): Completable
    fun invite(boxId: Int, email: String): Flowable<Invitation>
}