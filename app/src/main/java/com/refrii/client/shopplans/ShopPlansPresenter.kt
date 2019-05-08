package com.refrii.client.shopplans

import com.refrii.client.data.models.ShopPlan
import com.refrii.client.data.source.ApiRepository
import rx.Subscriber
import javax.inject.Inject

class ShopPlansPresenter
@Inject
constructor(private val mApiRepository: ApiRepository) : ShopPlansContract.Presenter {

    private var mView: ShopPlansContract.View? = null
    private var mShopPlans: List<ShopPlan>? = null

    override fun takeView(view: ShopPlansContract.View) {
        mView = view
    }

    override fun getShopPlans() {
        mApiRepository.getShopPlans()
                .subscribe(object : Subscriber<List<ShopPlan>>() {
                    override fun onNext(t: List<ShopPlan>?) {
                        mShopPlans = t
                        mView?.setShopPlans(t)
                    }

                    override fun onCompleted() {}

                    override fun onError(e: Throwable?) {
                    }
                })
    }
}