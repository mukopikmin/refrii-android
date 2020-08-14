package app.muko.mypantry.data.source.data

import app.muko.mypantry.data.models.ShopPlan
import io.reactivex.Completable
import io.reactivex.Flowable

interface ApiShopPlanDataSource {
    fun getAll(): Flowable<List<ShopPlan>>
    fun getByFood(foodId: Int): Flowable<List<ShopPlan>>
    fun get(id: Int): Flowable<ShopPlan?>
    fun create(shopPlan: ShopPlan): Completable
    fun update(shopPlan: ShopPlan): Completable
    fun remove(shopPlan: ShopPlan): Completable
}