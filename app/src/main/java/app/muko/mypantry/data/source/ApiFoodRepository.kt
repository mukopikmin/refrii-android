package app.muko.mypantry.data.source

import app.muko.mypantry.data.dao.FoodDao
import app.muko.mypantry.data.models.Food
import app.muko.mypantry.data.source.data.ApiFoodDataSource
import app.muko.mypantry.data.source.local.ApiLocalFoodSource
import app.muko.mypantry.data.source.remote.ApiRemoteFoodSource
import app.muko.mypantry.data.source.remote.services.FoodService
import io.reactivex.Completable
import io.reactivex.CompletableObserver
import io.reactivex.Flowable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.rxkotlin.toFlowable
import java.io.File

class ApiFoodRepository(service: FoodService, val dao: FoodDao) : ApiFoodDataSource {

    private val remote = ApiRemoteFoodSource(service)
    private val local = ApiLocalFoodSource(dao)

    override fun getByBox(boxId: Int): Flowable<List<Food>> {
        Flowable.zip(
                remote.getByBox(boxId),
                local.getByBox(boxId),
                BiFunction<List<Food>, List<Food>, Pair<List<Food>, List<Food>>> { r, l -> Pair(r, l) }
        ).flatMap { pair ->
            pair.second.forEach { local.remove(it) }
            pair.first.map { local.create(it) }.toFlowable()
        }.subscribe()

        return local.getByBox(boxId)
    }

    override fun get(id: Int): Flowable<Food?> {
        remote.get(id)
                .flatMap { local.create(it).toFlowable<Food>() }
                .subscribe()

        return local.get(id)
    }

    override fun create(food: Food): Completable {
        remote.create(food)
                .subscribe()

        return local.create(food)
    }

    override fun update(food: Food, imageFile: File?): Completable {
        remote.update(food, imageFile)
                .subscribe()

        return local.update(food, imageFile)
    }

    override fun remove(food: Food): Completable {
        remote.remove(food)
                .subscribe(object : CompletableObserver {
                    override fun onComplete() {}
                    override fun onSubscribe(d: Disposable) {}
                    override fun onError(e: Throwable) {
                        create(food)
                    }
                })

        return local.remove(food)
    }
}