package app.muko.mypantry.data.source

import app.muko.mypantry.data.dao.FoodDao
import app.muko.mypantry.data.models.Food
import app.muko.mypantry.data.source.data.ApiFoodDataSource
import app.muko.mypantry.data.source.local.ApiLocalFoodSource
import app.muko.mypantry.data.source.remote.ApiRemoteFoodSource
import app.muko.mypantry.data.source.remote.services.FoodService
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.functions.BiFunction
import java.io.File

class ApiFoodRepository(service: FoodService, val dao: FoodDao) : ApiFoodDataSource {

    private val remote = ApiRemoteFoodSource(service)
    private val local = ApiLocalFoodSource(dao)

    override fun getByBox(boxId: Int): Flowable<List<Food>> {
        return Flowable.zip(
                remote.getByBox(boxId),
                local.getByBox(boxId),
                BiFunction<List<Food>, List<Food>, Pair<List<Food>, List<Food>>> { r, l -> Pair(r, l) }
        ).flatMap { pair ->
            pair.second.forEach { local.remove(it) }
            pair.first.map { local.create(it) }

            Flowable.just(pair.first)
        }
    }

    override fun get(id: Int): Flowable<Food?> {
        return remote.get(id)
                .flatMap { local.create(it).toFlowable<Food>() }
    }

    override fun create(food: Food): Completable {
        return remote.create(food)
    }

    override fun update(food: Food, imageFile: File?): Completable {
        return remote.update(food, imageFile)
    }

    override fun remove(food: Food): Completable {
        return remote.remove(food)
                .andThen { local.remove(food) }
    }

    override fun createNotice(food: Food, text: String): Flowable<Food> {
//        TODO: Impl caching
        return remote.createNotice(food, text)
    }
}