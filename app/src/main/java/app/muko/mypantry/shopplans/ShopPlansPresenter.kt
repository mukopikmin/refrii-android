package app.muko.mypantry.shopplans

import app.muko.mypantry.data.models.ShopPlan
import app.muko.mypantry.data.source.ApiShopPlanRepository
import io.reactivex.CompletableObserver
import io.reactivex.disposables.Disposable
import io.reactivex.subscribers.DisposableSubscriber
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
        mApiShopPlanRepository.getAll()
                .subscribe(object : DisposableSubscriber<List<ShopPlan>>() {
                    override fun onNext(t: List<ShopPlan>?) {
                        mShopPlans = t
                        mView?.setShopPlans(t)
                    }

                    override fun onComplete() {}

                    override fun onError(e: Throwable?) {
                        mView?.showToast(e?.message)
                    }
                })
    }

    override fun completeShopPlan(shopPlan: ShopPlan) {
        shopPlan.done = true

        mApiShopPlanRepository.update(shopPlan)
                .subscribe(object : CompletableObserver {
                    override fun onComplete() {
                        mView?.showSnackBar("${shopPlan?.food?.name} の予定を完了しました")
                        getShopPlans()
                    }

                    override fun onSubscribe(d: Disposable) {}

                    override fun onError(e: Throwable) {
                        mView?.showToast(e.message)
                    }
                })
    }
}