package com.refrii.client.data.source

import android.content.Context
import android.graphics.Bitmap
import com.refrii.client.data.models.Box
import com.refrii.client.data.models.Food
import com.refrii.client.data.models.ShopPlan
import com.refrii.client.data.models.Unit
import com.refrii.client.data.source.local.ApiLocalFoodSource
import com.refrii.client.data.source.remote.ApiRemoteFoodSource
import io.realm.Realm
import retrofit2.Retrofit
import rx.Observable
import java.util.*

class ApiFoodRepository(context: Context, realm: Realm, retrofit: Retrofit) {

    private val mApiRemoteFoodSource = ApiRemoteFoodSource(context, retrofit)
    private val mApiLocalFoodSource = ApiLocalFoodSource(realm)

    fun getFoods(): Observable<List<Food>> {
        return Observable.zip(
                mApiRemoteFoodSource.getFoods(),
                mApiLocalFoodSource.getFoods()
        ) { remote, cache -> Pair(remote, cache) }
                .flatMap { pair ->
                    val remote = pair.first
                    val cache = pair.second

                    remote.forEach { mApiLocalFoodSource.saveFood(it) }

                    cache.forEach { box ->
                        if (!remote.map { it.id }.contains(box.id)) {
                            mApiLocalFoodSource.removeFood(box.id)
                        }
                    }

                    mApiLocalFoodSource.getFoods()
                }
    }


    fun getFoodsFromCache(): Observable<List<Food>> {
        return mApiLocalFoodSource.getFoods()
    }

    fun getFood(id: Int): Observable<Food?> {
        return mApiRemoteFoodSource.getFood(id)
                .flatMap { mApiLocalFoodSource.saveFood(it) }
    }

    fun getFoodFromCache(id: Int): Observable<Food?> {
        return mApiLocalFoodSource.getFood(id)
    }

    fun getShopPlansForFood(id: Int): Observable<List<ShopPlan>> {
        return mApiRemoteFoodSource.getShopPlansForFood(id)
    }

    fun getShopPlansForFoodFromCache(id: Int): Observable<List<ShopPlan>> {
        return mApiLocalFoodSource.getShopPlansForFood(id)
    }

    fun getExpiringFoods(): Observable<List<Food>> {
        return mApiLocalFoodSource.getExpiringFoods()
    }

    fun getExpiringFoodsFromCache(): Observable<List<Food>> {
        return mApiLocalFoodSource.getExpiringFoods()
    }

    fun createFood(name: String, amount: Double, box: Box, unit: Unit, expirationDate: Date): Observable<Food> {
        return mApiRemoteFoodSource.createFood(name, amount, box, unit, expirationDate)
                .flatMap { mApiLocalFoodSource.saveFood(it) }
    }

    fun updateFood(id: Int, name: String?, amount: Double?, expirationDate: Date?, bitmap: Bitmap?, boxId: Int?, unitId: Int?): Observable<Food> {
        return mApiRemoteFoodSource.update(id, name, amount, expirationDate, bitmap, boxId, unitId)
                .flatMap {
                    mApiLocalFoodSource.updateFood(it.id, it.name, it.amount, it.expirationDate, it.imageUrl, it.box?.id, it.unit?.id)
                }
    }

    fun removeFood(id: Int): Observable<Void> {
        return mApiRemoteFoodSource.removeFood(id)
    }

    fun createNotice(foodId: Int, text: String): Observable<Food> {
        return mApiRemoteFoodSource.createNotice(foodId, text)
                .flatMap { mApiLocalFoodSource.saveFood(it) }
    }
}