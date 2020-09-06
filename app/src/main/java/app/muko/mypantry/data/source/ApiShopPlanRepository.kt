package app.muko.mypantry.data.source

import app.muko.mypantry.data.dao.ShopPlanDao
import app.muko.mypantry.data.models.ShopPlan
import app.muko.mypantry.data.source.data.ApiShopPlanDataSource
import app.muko.mypantry.data.source.local.ApiLocalShopPlanSource
import app.muko.mypantry.data.source.remote.ApiRemoteShopPlanSource
import app.muko.mypantry.data.source.remote.services.ShopPlanService
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.functions.BiFunction

class ApiShopPlanRepository(
        service: ShopPlanService,
        val dao: ShopPlanDao
) : ApiShopPlanDataSource {

    private val remote = ApiRemoteShopPlanSource(service)
    private val local = ApiLocalShopPlanSource(dao)

    override fun getAll(): Flowable<List<ShopPlan>> {
        return Flowable.zip(
                remote.getAll(),
                local.getAll(),
                BiFunction<List<ShopPlan>, List<ShopPlan>, Pair<List<ShopPlan>, List<ShopPlan>>> { r, l -> Pair(r, l) }
        ).flatMap { pair ->
            pair.second.forEach { local.remove(it) }
            pair.first.map { local.create(it) }

            Flowable.just(pair.first)
        }
    }

    override fun getByFood(foodId: Int): Flowable<List<ShopPlan>> {
        return Flowable.zip(
                remote.getByFood(foodId),
                local.getByFood(foodId),
                BiFunction<List<ShopPlan>, List<ShopPlan>, Pair<List<ShopPlan>, List<ShopPlan>>> { r, l -> Pair(r, l) }
        ).flatMap { pair ->
            pair.second.forEach { local.remove(it) }
            pair.first.map { local.create(it) }

            Flowable.just(pair.first)
        }
    }

    override fun get(id: Int): Flowable<ShopPlan?> {
        TODO("Not yet implemented")
    }

    override fun create(shopPlan: ShopPlan): Completable {
        return remote.create(shopPlan)
    }

    override fun update(shopPlan: ShopPlan): Completable {
        return remote.update(shopPlan)
    }

    override fun remove(shopPlan: ShopPlan): Completable {
        return remote.remove(shopPlan)
    }
}