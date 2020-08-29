package app.muko.mypantry.data.source.local

import app.muko.mypantry.data.dao.FoodDao
import app.muko.mypantry.data.models.Food
import app.muko.mypantry.data.source.data.ApiFoodDataSource
import io.reactivex.Completable
import io.reactivex.Flowable
import java.io.File

class ApiLocalFoodSource(
        private val dao: FoodDao
) : ApiFoodDataSource {

    override fun getByBox(boxId: Int): Flowable<List<Food>> {
        val foods = dao.getAll()
                .filter { it.box.id == boxId }

        return Flowable.just(foods)
    }

    override fun get(id: Int): Flowable<Food?> {
        return Flowable.just(dao.get(id))
    }

    override fun create(food: Food): Completable {
        dao.insertOrUpdate(food)

        return Completable.complete()
    }

    override fun update(food: Food, imageFile: File?): Completable {
        dao.insertOrUpdate(food)

        return Completable.complete()
    }

    override fun remove(food: Food): Completable {
        dao.delete(food)

        return Completable.complete()
    }

    override fun createNotice(food: Food, text: String): Flowable<Food> {
        TODO("Not yet implemented")
    }
}