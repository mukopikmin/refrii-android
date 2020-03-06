package app.muko.mypantry.data.source.local

import app.muko.mypantry.data.models.Box
import app.muko.mypantry.data.models.Food
import app.muko.mypantry.data.models.ShopPlan
import app.muko.mypantry.data.models.Unit
import io.realm.Realm
import io.realm.kotlin.where
import rx.Observable
import java.util.*

class ApiLocalFoodSource(private val mRealm: Realm) {

    fun getFoods(): Observable<List<Food>> {
        val foods = mRealm.where<Food>()
                .findAll()
                .sort("id")

        return Observable.just(mRealm.copyFromRealm(foods))
    }

    fun getFood(id: Int): Observable<Food?> {
        val food = mRealm.where<Food>()
                .equalTo("id", id)
                .findFirst()

        return Observable.just(mRealm.copyFromRealm(food))
    }

    fun getExpiringFoods(): Observable<List<Food>> {
        val week = 7 * 24 * 60 * 60 * 1000 // 7 days
        val now = Date().time
        val foods = mRealm.where<Food>()
                .findAll()
                .filter { it.expirationDate!!.time - now < week }
                .sortedBy { it.expirationDate }

        return Observable.just(mRealm.copyFromRealm(foods))
    }

    fun updateFood(id: Int, name: String?, amount: Double?, expirationDate: Date?, imageUrl: String?, boxId: Int?, unitId: Int?): Observable<Food> {
        val food = mRealm.where<Food>()
                .equalTo("id", id)
                .findFirst() ?: return Observable.error(Throwable("見つかりませんでした"))

        mRealm.executeTransaction { realm ->
            val box = realm.where<Box>()
                    .equalTo("id", boxId)
                    .findFirst()
            val unit = realm.where<Unit>()
                    .equalTo("id", unitId)
                    .findFirst()

            name?.let { food.name = it }
            amount?.let { food.amount = it }
            expirationDate?.let { food.expirationDate = it }
            imageUrl?.let { food.imageUrl = it }
            box?.let { food.box = it }
            unit?.let { food.unit = it }
        }

        return Observable.just(mRealm.copyFromRealm(food))
    }

    fun saveFood(food: Food): Observable<Food?> {
        mRealm.executeTransaction {
            it.copyToRealmOrUpdate(food)
        }

        return getFood(food.id)
    }


    fun createFood(name: String, amount: Double, box: Box, unit: Unit, expirationDate: Date) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun removeFood(id: Int) {
        val food = mRealm.where<Food>()
                .equalTo("id", id)
                .findFirst()

        mRealm.executeTransaction {
            food?.deleteFromRealm()
        }
    }


    fun getShopPlansForFood(foodId: Int): Observable<List<ShopPlan>> {
        val plans = mRealm.where<ShopPlan>()
                .equalTo("food.id", foodId)
                .findAll()

        return Observable.just(mRealm.copyFromRealm(plans))
    }
}