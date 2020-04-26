package app.muko.mypantry.data.source

import app.muko.mypantry.data.models.ShopPlan
import app.muko.mypantry.data.source.remote.ApiRemoteShopPlanSource
import retrofit2.Retrofit
import rx.Observable
import java.util.*

class ApiShopPlanRepository( retrofit: Retrofit) {

    private val mApiRemoteShopPlanSource = ApiRemoteShopPlanSource(retrofit)

    fun getShopPlans(): Observable<List<ShopPlan>> {
        return mApiRemoteShopPlanSource.getShopPlans()
    }

//    fun getShopPlansFromCache(): Observable<List<ShopPlan>> {
//        return mApiLocalShopPlanSource.getShopPlans()
//    }

    fun createShopPlan(foodId: Int, amount: Double, date: Date): Observable<ShopPlan> {
        return mApiRemoteShopPlanSource.createShopPlan(foodId, amount, date)
    }

    fun updateShopPlan(id: Int, done: Boolean?): Observable<ShopPlan> {
        return mApiRemoteShopPlanSource.updateShopPlan(id, done)
    }
}