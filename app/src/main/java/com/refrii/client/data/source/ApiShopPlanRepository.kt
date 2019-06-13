package com.refrii.client.data.source

import com.refrii.client.data.models.ShopPlan
import com.refrii.client.data.source.local.ApiLocalShopPlanSource
import com.refrii.client.data.source.remote.ApiRemoteShopPlanSource
import io.realm.Realm
import retrofit2.Retrofit
import rx.Observable
import java.util.*

class ApiShopPlanRepository(realm: Realm, retrofit: Retrofit) {

    private val mApiRemoteShopPlanSource = ApiRemoteShopPlanSource(retrofit)
    private val mApiLocalShopPlanSource = ApiLocalShopPlanSource(realm)

    fun getShopPlans(): Observable<List<ShopPlan>> {
        return mApiRemoteShopPlanSource.getShopPlans()
                .flatMap { mApiLocalShopPlanSource.saveShopPlans(it) }
    }

    fun getShopPlansFromCache(): Observable<List<ShopPlan>> {
        return mApiLocalShopPlanSource.getShopPlans()
    }

    fun createShopPlan(foodId: Int, amount: Double, date: Date): Observable<ShopPlan> {
        return mApiRemoteShopPlanSource.createShopPlan(foodId, amount, date)
                .flatMap { mApiLocalShopPlanSource.saveShopPlan(it) }
    }

    fun completeShopPlan(id: Int): Observable<ShopPlan> {
        return mApiRemoteShopPlanSource.completeShopPlan(id)
                .flatMap { mApiLocalShopPlanSource.saveShopPlan(it) }
    }
}