package app.muko.mypantry.food

import android.graphics.Bitmap
import app.muko.mypantry.data.models.Food
import app.muko.mypantry.data.models.ShopPlan
import app.muko.mypantry.data.models.Unit
import app.muko.mypantry.data.source.ApiBoxRepository
import app.muko.mypantry.data.source.ApiFoodRepository
import app.muko.mypantry.data.source.ApiShopPlanRepository
import io.reactivex.subscribers.DisposableSubscriber
import java.util.*
import javax.inject.Inject

class FoodPresenter
@Inject
constructor(
        private val mApiFoodRepository: ApiFoodRepository,
        private val mApiBoxRepository: ApiBoxRepository,
        private val mApiShopPlanRepository: ApiShopPlanRepository
) : FoodContract.Presenter {

    private var mView: FoodContract.View? = null

    override fun takeView(view: FoodContract.View) {
        mView = view
    }

    override fun getFood(id: Int) {
        mApiFoodRepository.getFood(id)
                .subscribe(object : DisposableSubscriber<Food>() {
                    override fun onNext(t: Food?) {
                        mView?.setFood(t)
                    }

                    override fun onComplete() {}

                    override fun onError(e: Throwable?) {
                        mView?.showToast(e?.message)
                    }
                })
    }

    override fun getUnits(boxId: Int) {
        mApiBoxRepository.getUnitsForBox(boxId)
                .subscribe(object : DisposableSubscriber<List<Unit>>() {
                    override fun onNext(t: List<Unit>?) {
                        mView?.setUnits(t)
                    }

                    override fun onComplete() {}

                    override fun onError(e: Throwable?) {
                        mView?.showToast(e?.message)
                    }
                })
    }

    override fun updateFood(id: Int, name: String?, amount: Double?, expirationDate: Date?, image: Bitmap?, boxId: Int?, unitId: Int?) {
        mApiFoodRepository.updateFood(id, name, amount, expirationDate, image, boxId, unitId)
                .doOnSubscribe { mView?.showProgressBar() }
                .doFinally { mView?.hideProgressBar() }
                .subscribe(object : DisposableSubscriber<Food>() {
                    override fun onNext(t: Food?) {
                        mView?.onUpdateCompleted(t)
                    }

                    override fun onComplete() {}

                    override fun onError(e: Throwable?) {
                        mView?.showToast(e?.message)
                    }
                })
    }

    override fun getShopPlans(id: Int) {
        mApiFoodRepository.getShopPlansForFood(id)
                .subscribe(object : DisposableSubscriber<List<ShopPlan>>() {
                    override fun onNext(t: List<ShopPlan>?) {
                        mView?.setShopPlans(t?.filter { !it.done })
                    }

                    override fun onComplete() {}

                    override fun onError(e: Throwable?) {
                        mView?.showToast(e?.message)
                    }
                })
    }

    override fun createShopPlan(amount: Double, date: Date, foodId: Int) {
        mApiShopPlanRepository.createShopPlan(foodId, amount, date)
                .doOnSubscribe { mView?.showProgressBar() }
                .doFinally { mView?.hideProgressBar() }
                .subscribe(object : DisposableSubscriber<ShopPlan>() {
                    override fun onNext(t: ShopPlan?) {}

                    override fun onComplete() {
                        getShopPlans(foodId)
                    }

                    override fun onError(e: Throwable?) {
                        mView?.showToast(e?.message)
                    }
                })
    }

    override fun completeShopPlan(shopPlan: ShopPlan) {
        mApiShopPlanRepository.updateShopPlan(shopPlan.id, true)
                .doOnSubscribe { mView?.showProgressBar() }
                .doFinally { mView?.hideProgressBar() }
                .subscribe(object : DisposableSubscriber<ShopPlan>() {
                    override fun onNext(t: ShopPlan?) {
                        mView?.onCompletedCompleteShopPlan(t)
                        t?.food?.id?.let {
                            getFood(it)
                        }
                    }

                    override fun onComplete() {}

                    override fun onError(e: Throwable?) {
                        mView?.showToast(e?.message)
                    }
                })
    }
}