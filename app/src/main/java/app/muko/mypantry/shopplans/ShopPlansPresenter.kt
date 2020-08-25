package app.muko.mypantry.shopplans

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import app.muko.mypantry.data.models.ShopPlan
import app.muko.mypantry.data.source.ApiShopPlanRepository
import io.reactivex.CompletableObserver
import io.reactivex.disposables.Disposable
import io.reactivex.subscribers.DisposableSubscriber
import javax.inject.Inject

class ShopPlansPresenter
@Inject
constructor(
        private val apiShopPlanRepository: ApiShopPlanRepository
) : ShopPlansContract.Presenter {

    private lateinit var view: ShopPlansContract.View
    private lateinit var shopPlansLiveData: LiveData<List<ShopPlan>>

    override fun init(view: ShopPlansContract.View) {
        this.view = view as ShopPlansActivity
        shopPlansLiveData = apiShopPlanRepository.dao.getAllLiveData()

        shopPlansLiveData.observe(view, Observer {
            view.setShopPlans(it)
        })
    }

    override fun getShopPlans() {
        apiShopPlanRepository.getAll()
                .subscribe(object : DisposableSubscriber<List<ShopPlan>>() {
                    override fun onNext(t: List<ShopPlan>?) {}

                    override fun onComplete() {}

                    override fun onError(e: Throwable?) {
                        view.showToast(e?.message)
                    }
                })
    }

    override fun completeShopPlan(shopPlan: ShopPlan) {
        shopPlan.done = true

        apiShopPlanRepository.update(shopPlan)
                .subscribe(object : CompletableObserver {
                    override fun onComplete() {
                        view.showSnackBar("${shopPlan.food.name} の予定を完了しました")
                        getShopPlans()
                    }

                    override fun onSubscribe(d: Disposable) {}

                    override fun onError(e: Throwable) {
                        view.showToast(e.message)
                    }
                })
    }
}