package app.muko.mypantry.data.source.data

import app.muko.mypantry.data.models.Box
import io.reactivex.Completable
import io.reactivex.Flowable

interface ApiBoxDataSource {

    fun getAll(): Flowable<List<Box>>
    fun get(id: Int): Flowable<Box?>
    fun create(box: Box): Completable
    fun update(box: Box): Completable
    fun remove(box: Box): Completable
}