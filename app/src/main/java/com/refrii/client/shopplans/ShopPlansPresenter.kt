package com.refrii.client.shopplans

import com.refrii.client.data.models.ShopPlan
import com.refrii.client.data.source.ApiShopPlanRepository
import rx.Subscriber
import javax.inject.Inject

class ShopPlansPresenter
@Inject
constructor(private val mApiShopPlanRepository: ApiShopPlanRepository) : ShopPlansContract.Presenter {

    private var mView: ShopPlansContract.View? = null
    private var mShopPlans: List<ShopPlan>? = null

    override fun takeView(view: ShopPlansContract.View) {
        mView = view
    }

    override fun getShopPlans() {
        mApiShopPlanRepository.getShopPlansFromCache()
                .subscribe(object : Subscriber<List<ShopPlan>>() {
                    override fun onNext(t: List<ShopPlan>?) {
                        mShopPlans = t
                        mView?.setShopPlans(t)
                    }

                    override fun onCompleted() {}

                    override fun onError(e: Throwable?) {
                        mView?.showToast(e?.message)
                    }
                })

        mApiShopPlanRepository.getShopPlans()
                .subscribe(object : Subscriber<List<ShopPlan>>() {
                    override fun onNext(t: List<ShopPlan>?) {
                        mShopPlans = t
                        mView?.setShopPlans(t)
                    }

                    override fun onCompleted() {}

                    override fun onError(e: Throwable?) {
                        mView?.showToast(e?.message)
                    }
                })
    }

    override fun completeShopPlan(shopPlan: ShopPlan) {
        mApiShopPlanRepository.updateShopPlan(shopPlan.id, true)
                .subscribe(object : Subscriber<ShopPlan>() {
                    override fun onNext(t: ShopPlan?) {
                        mView?.showSnackBar("${t?.food?.name} の予定を完了しました")
                    }

                    override fun onCompleted() {
                        getShopPlans()
                    }

                    override fun onError(e: Throwable?) {
                        mView?.showToast(e?.message)
                    }
                })
    }
}