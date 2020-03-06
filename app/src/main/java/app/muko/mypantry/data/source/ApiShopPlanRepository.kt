package app.muko.mypantry.data.source

import app.muko.mypantry.data.models.ShopPlan
import app.muko.mypantry.data.source.local.ApiLocalShopPlanSource
import app.muko.mypantry.data.source.remote.ApiRemoteShopPlanSource
import io.realm.Realm
import retrofit2.Retrofit
import rx.Observable
import java.util.*

class ApiShopPlanRepository(realm: Realm, retrofit: Retrofit) {

    private val mApiRemoteShopPlanSource = ApiRemoteShopPlanSource(retrofit)
    private val mApiLocalShopPlanSource = ApiLocalShopPlanSource(realm)

    fun getShopPlans(): Observable<List<ShopPlan>> {
        return Observable.zip(
                mApiRemoteShopPlanSource.getShopPlans(),
                mApiLocalShopPlanSource.getShopPlans()
        ) { remote, cache -> Pair(remote, cache) }
                .flatMap { pair ->
                    val remote = pair.first
                    val cache = pair.second

                    remote.forEach { mApiLocalShopPlanSource.saveShopPlan(it) }

                    cache.forEach { plan ->
                        val ids = remote.map { it.id }

                        if (!ids.contains(plan.id)) {
                            mApiLocalShopPlanSource.complete(plan.id)
                        }
                    }

                    mApiLocalShopPlanSource.getShopPlans()
                }
    }

    fun getShopPlansFromCache(): Observable<List<ShopPlan>> {
        return mApiLocalShopPlanSource.getShopPlans()
    }

    fun createShopPlan(foodId: Int, amount: Double, date: Date): Observable<ShopPlan> {
        return mApiRemoteShopPlanSource.createShopPlan(foodId, amount, date)
                .flatMap { mApiLocalShopPlanSource.saveShopPlan(it) }
    }

    fun updateShopPlan(id: Int, done: Boolean?): Observable<ShopPlan> {
        return mApiRemoteShopPlanSource.updateShopPlan(id, done)
                .flatMap { mApiLocalShopPlanSource.saveShopPlan(it) }
    }
}