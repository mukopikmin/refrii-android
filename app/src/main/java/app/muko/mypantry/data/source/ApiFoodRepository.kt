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

class ApiFoodRepository(
        private val service: FoodService,
        private val dao: FoodDao
) : ApiFoodDataSource {

    private val remote = ApiRemoteFoodSource(service)

    //    private val mAPiLocalFoodSource = ApiLocalFoodSource(room)
//    private val mAPiLocalShopPlanSource = ApiLocalShopPlanSource(room)
//    private val mShopPlanDao = room.shopPlanDao()
    private val local = ApiLocalFoodSource(dao)


//    fun createFood(name: String, amount: Double, box: Box, unit: Unit, expirationDate: Date): Flowable<Food> {
//        return remote.createFood(name, amount, box, unit, expirationDate)
//    }
//
//    fun updateFood(id: Int, name: String?, amount: Double?, expirationDate: Date?, bitmap: Bitmap?, boxId: Int?, unitId: Int?): Flowable<Food> {
//        return remote.update(id, name, amount, expirationDate, bitmap, boxId, unitId)
//    }
//
//    fun removeFood(id: Int): Flowable<Void> {
//        return remote.removeFood(id)
//    }
//
//    fun createNotice(foodId: Int, text: String): Flowable<Food> {
//        return remote.createNotice(foodId, text)
//    }

    override fun getByBox(boxId: Int): Flowable<List<Food>> {
        local.getByBox(boxId)
                .flatMap { foods ->
                    foods.map { local.remove(it) }

                    remote.getByBox(boxId)
                }
                .flatMap { foods ->
                    Flowable.just(foods.map { local.create(it) })
                }
                .subscribe()

        return local.getByBox(boxId)
    }

    override fun get(id: Int): Flowable<Food?> {
        remote.get(id)
                .flatMap {
                    local.create(it)

                    Flowable.just(it)
                }
                .subscribe()

        return local.get(id)
    }

    override fun create(food: Food): Completable {
        remote.create(food)
                .andThen { local.create(food) }
                .subscribe()

        return local.create(food)
    }

    override fun update(food: Food): Completable {
        remote.update(food)
                .andThen { local.update(food) }
                .subscribe()

        return local.update(food)
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