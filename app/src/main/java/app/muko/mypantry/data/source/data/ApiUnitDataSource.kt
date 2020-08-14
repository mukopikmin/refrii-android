package app.muko.mypantry.data.source.data

import app.muko.mypantry.data.models.Box
import app.muko.mypantry.data.models.Unit
import io.reactivex.Completable
import io.reactivex.Flowable

interface ApiUnitDataSource {
    fun getAll(): Flowable<List<Unit>>
    fun getByBox(box: Box): Flowable<List<Unit>>
    fun get(id: Int): Flowable<Unit?>
    fun create(unit: Unit): Completable
    fun update(unit: Unit): Completable
    fun remove(unit: Unit): Completable
}