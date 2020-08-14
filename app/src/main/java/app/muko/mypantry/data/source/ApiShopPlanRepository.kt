package app.muko.mypantry.data.source

import app.muko.mypantry.data.dao.ShopPlanDao
import app.muko.mypantry.data.models.ShopPlan
import app.muko.mypantry.data.source.data.ApiShopPlanDataSource
import app.muko.mypantry.data.source.local.ApiLocalShopPlanSource
import app.muko.mypantry.data.source.remote.ApiRemoteShopPlanSource
import app.muko.mypantry.data.source.remote.services.ShopPlanService
import io.reactivex.Completable
import io.reactivex.CompletableObserver
import io.reactivex.Flowable
import io.reactivex.disposables.Disposable

class ApiShopPlanRepository(
        private val service: ShopPlanService,
        private val dao: ShopPlanDao
) : ApiShopPlanDataSource {

    private val remote = ApiRemoteShopPlanSource(service)
    private val local = ApiLocalShopPlanSource(dao)

//    fun getShopPlans(): Flowable<List<ShopPlan>> {
//        return mApiRemoteShopPlanSource.getShopPlans()
//    }
//
////    fun getShopPlansFromCache(): Flowable<List<ShopPlan>> {
////        return mApiLocalShopPlanSource.getShopPlans()
////    }
//
//    fun createShopPlan(foodId: Int, amount: Double, date: Date): Flowable<ShopPlan> {
//        return mApiRemoteShopPlanSource.createShopPlan(foodId, amount, date)
//                .flatMap {
//                    mApiLocalShopPlanSource.create(it)
//                }
//    }
//
//    fun updateShopPlan(id: Int, done: Boolean?): Flowable<ShopPlan> {
//        return mApiRemoteShopPlanSource.updateShopPlan(id, done)
//    }

    override fun getAll(): Flowable<List<ShopPlan>> {
        local.getAll()
                .flatMap { plans ->
                    plans.map { local.remove(it) }

                    remote.getAll()
                }
                .flatMap { plans ->
                    Flowable.just(plans.map { local.create(it) })
                }
                .subscribe()

        return local.getAll()

    }

    override fun getByFood(foodId: Int): Flowable<List<ShopPlan>> {
        local.getByFood(foodId)
                .flatMap { plans ->
                    plans.map { local.remove(it) }

                    remote.getByFood(foodId)
                }
                .flatMap { plans ->
                    Flowable.just(plans.map { local.create(it) })
                }
                .subscribe()

        return local.getByFood(foodId)
    }

    override fun get(id: Int): Flowable<ShopPlan?> {
        TODO("Not yet implemented")
    }

    override fun create(shopPlan: ShopPlan): Completable {
        remote.create(shopPlan)
                .subscribe()

        return local.create(shopPlan)
    }

    override fun update(shopPlan: ShopPlan): Completable {
        remote.update(shopPlan)
                .subscribe()

        return local.update(shopPlan)
    }

    override fun remove(shopPlan: ShopPlan): Completable {
        remote.remove(shopPlan)
                .subscribe(object : CompletableObserver {
                    override fun onComplete() {}

                    override fun onSubscribe(d: Disposable) {}

                    override fun onError(e: Throwable) {
                        create(shopPlan)
                    }
                })


        return local.remove(shopPlan)
    }
}