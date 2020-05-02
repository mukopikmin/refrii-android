package app.muko.mypantry.data.source

import app.muko.mypantry.data.models.ShopPlan
import app.muko.mypantry.data.source.remote.ApiRemoteShopPlanSource
import io.reactivex.Flowable
import retrofit2.Retrofit
import java.util.*

class ApiShopPlanRepository( retrofit: Retrofit) {

    private val mApiRemoteShopPlanSource = ApiRemoteShopPlanSource(retrofit)

    fun getShopPlans(): Flowable<List<ShopPlan>> {
        return mApiRemoteShopPlanSource.getShopPlans()
    }

//    fun getShopPlansFromCache(): Flowable<List<ShopPlan>> {
//        return mApiLocalShopPlanSource.getShopPlans()
//    }

    fun createShopPlan(foodId: Int, amount: Double, date: Date): Flowable<ShopPlan> {
        return mApiRemoteShopPlanSource.createShopPlan(foodId, amount, date)
    }

    fun updateShopPlan(id: Int, done: Boolean?): Flowable<ShopPlan> {
        return mApiRemoteShopPlanSource.updateShopPlan(id, done)
    }
}