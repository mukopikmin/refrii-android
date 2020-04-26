package app.muko.mypantry.food

import android.graphics.Bitmap
import app.muko.mypantry.data.models.Food
import app.muko.mypantry.data.models.ShopPlan
import app.muko.mypantry.data.models.Unit
import app.muko.mypantry.data.source.ApiBoxRepository
import app.muko.mypantry.data.source.ApiFoodRepository
import app.muko.mypantry.data.source.ApiShopPlanRepository
import rx.Subscriber
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
        mApiFoodRepository.getFoodFromCache(id)
                .subscribe(object : Subscriber<Food>() {
                    override fun onNext(t: Food?) {
                        mView?.setFood(t)
                    }

                    override fun onCompleted() { }

                    override fun onError(e: Throwable?) {
                        mView?.showToast(e?.message)
                    }
                })

        mApiFoodRepository.getFood(id)
                .subscribe(object : Subscriber<Food>() {
                    override fun onNext(t: Food?) {
                        mView?.setFood(t)
                    }

                    override fun onCompleted() { }

                    override fun onError(e: Throwable?) {
                        mView?.showToast(e?.message)
                    }
                })
    }

    override fun getUnits(boxId: Int) {
        mApiBoxRepository.getUnitsForBoxFromCache(boxId)
                .subscribe(object : Subscriber<List<Unit>>() {
                    override fun onNext(t: List<Unit>?) {
                        mView?.setUnits(t)
                    }

                    override fun onCompleted() {}

                    override fun onError(e: Throwable?) {
                        mView?.showToast(e?.message)
                    }

                })

        mApiBoxRepository.getUnitsForBox(boxId)
                .subscribe(object : Subscriber<List<Unit>>() {
                    override fun onNext(t: List<Unit>?) {
                        mView?.setUnits(t)
                    }

                    override fun onCompleted() {}

                    override fun onError(e: Throwable?) {
                        mView?.showToast(e?.message)
                    }
                })
    }

    override fun updateFood(id: Int, name: String?, amount: Double?, expirationDate: Date?, image: Bitmap?, boxId: Int?, unitId: Int?) {
        mApiFoodRepository.updateFood(id, name, amount, expirationDate, image, boxId, unitId)
                .doOnSubscribe { mView?.showProgressBar() }
                .doOnUnsubscribe { mView?.hideProgressBar() }
                .subscribe(object : Subscriber<Food>() {
                    override fun onNext(t: Food?) {
                        mView?.onUpdateCompleted(t)
                    }

                    override fun onCompleted() {}

                    override fun onError(e: Throwable?) {
                        mView?.showToast(e?.message)
                    }
                })
    }

    override fun getShopPlans(id: Int) {
        mApiFoodRepository.getShopPlansForFoodFromCache(id)
                .subscribe(object : Subscriber<List<ShopPlan>>() {
                    override fun onNext(t: List<ShopPlan>?) {
                        mView?.setShopPlans(t?.filter { !it.done })
                    }

                    override fun onCompleted() {}

                    override fun onError(e: Throwable?) {
                        mView?.showToast(e?.message)
                    }
                })

        mApiFoodRepository.getShopPlansForFood(id)
                .subscribe(object : Subscriber<List<ShopPlan>>() {
                    override fun onNext(t: List<ShopPlan>?) {
                        mView?.setShopPlans(t?.filter { !it.done })
                    }

                    override fun onCompleted() {}

                    override fun onError(e: Throwable?) {
                        mView?.showToast(e?.message)
                    }
                })
    }

    override fun createShopPlan(amount: Double, date: Date, foodId: Int) {
        mApiShopPlanRepository.createShopPlan(foodId, amount, date)
                .doOnSubscribe { mView?.showProgressBar() }
                .doOnUnsubscribe { mView?.hideProgressBar() }
                .subscribe(object : Subscriber<ShopPlan>() {
                    override fun onNext(t: ShopPlan?) {}

                    override fun onCompleted() {
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
                .doOnUnsubscribe { mView?.hideProgressBar() }
                .subscribe(object : Subscriber<ShopPlan>() {
                    override fun onNext(t: ShopPlan?) {
                        mView?.onCompletedCompleteShopPlan(t)
                        t?.food?.id?.let {
                            getFood(it)
                        }
                    }

                    override fun onCompleted() { }

                    override fun onError(e: Throwable?) {
                        mView?.showToast(e?.message)
                    }
                })
    }
}