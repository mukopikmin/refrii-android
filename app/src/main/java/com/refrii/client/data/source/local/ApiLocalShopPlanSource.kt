package com.refrii.client.data.source.local

import com.refrii.client.data.models.ShopPlan
import io.realm.Realm
import io.realm.kotlin.where
import rx.Observable

class ApiLocalShopPlanSource(private val mRealm: Realm) {
    fun getShopPlans(): Observable<List<ShopPlan>> {
        val plans = mRealm.where<ShopPlan>()
                .equalTo("done", false)
                .sort("date")
                .findAll()

        return Observable.just(mRealm.copyFromRealm(plans))
    }

    fun getShopPlan(id: Int): Observable<ShopPlan> {
        val plan = mRealm.where<ShopPlan>()
                .equalTo("id", id)
                .findFirst()

        return Observable.just(mRealm.copyFromRealm(plan))
    }

    fun complete(id: Int) {
        mRealm.executeTransaction { realm ->
            val plan = realm.where<ShopPlan>()
                    .equalTo("id", id)
                    .findFirst()

            plan?.done = true
        }
    }

    fun saveShopPlan(shopPlan: ShopPlan): Observable<ShopPlan> {
        mRealm.executeTransaction {
            it.copyToRealmOrUpdate(shopPlan)
        }

        return getShopPlan(shopPlan.id)
    }
}