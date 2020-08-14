package app.muko.mypantry.data.source.data

import app.muko.mypantry.data.models.Food
import io.reactivex.Completable
import io.reactivex.Flowable

interface ApiFoodDataSource {

    fun getByBox(boxId: Int): Flowable<List<Food>>
    fun get(id: Int): Flowable<Food?>
    fun create(food: Food): Completable
    fun update(food: Food): Completable
    fun remove(food: Food): Completable
}