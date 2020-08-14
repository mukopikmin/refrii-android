package app.muko.mypantry.data.source.local

import app.muko.mypantry.data.dao.ShopPlanDao
import app.muko.mypantry.data.models.ShopPlan
import app.muko.mypantry.data.source.data.ApiShopPlanDataSource
import io.reactivex.Completable
import io.reactivex.Flowable

class ApiLocalShopPlanSource(
        private val dao: ShopPlanDao
) : ApiShopPlanDataSource {

    override fun getAll(): Flowable<List<ShopPlan>> {
        return Flowable.just(dao.getAll())
    }

    override fun getByFood(foodId: Int): Flowable<List<ShopPlan>> {
        return Flowable.just(dao.getByFood(foodId))
    }

    override fun get(id: Int): Flowable<ShopPlan?> {
        return Flowable.just(dao.get(id))
    }

    override fun create(shopPlan: ShopPlan): Completable {
        dao.insertOrUpdate(shopPlan)

        return Completable.complete()
    }

    override fun update(shopPlan: ShopPlan): Completable {
        dao.insertOrUpdate(shopPlan)

        return Completable.complete()
    }

    override fun remove(shopPlan: ShopPlan): Completable {
        dao.delete(shopPlan)

        return Completable.complete()
    }
}