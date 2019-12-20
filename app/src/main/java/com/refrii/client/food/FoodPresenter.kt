package com.refrii.client.food

import android.graphics.Bitmap
import com.refrii.client.data.models.Food
import com.refrii.client.data.models.ShopPlan
import com.refrii.client.data.models.Unit
import com.refrii.client.data.source.ApiBoxRepository
import com.refrii.client.data.source.ApiFoodRepository
import com.refrii.client.data.source.ApiShopPlanRepository
import rx.Subscriber
import java.util.*
import javax.inject.Inject

class FoodPresenter
@Inject
constructor(
        private val mApiFoodRepository: ApiFoodRepository,
        private val mApiBoxRepository: ApiBoxRepository,
        private val mApiShopPlanRepository: ApiShopPlanRepository) : FoodContract.Presenter {

    private var mView: FoodContract.View? = null
    private var mFood: Food? = null
    private var mUnits: List<Unit>? = null
    private var mId: Int? = null
    private var mName: String? = null
    private var mAmount: Double? = null
    private var mNotice: String? = null
    private var mExpirationDate: Date? = null
    private var mImage: Bitmap? = null
    private var mBoxId: Int? = null
    private var mUnitId: Int? = null
    private var mImageUrl: String? = null

    override fun takeView(view: FoodContract.View) {
        mView = view
    }

    fun setFood(food: Food?) {
        mFood = food
        mId = food?.id
        mName = food?.name
        mAmount = food?.amount
        mExpirationDate = food?.expirationDate
        mBoxId = food?.box?.id
        mUnitId = food?.unit?.id
        mImageUrl = food?.imageUrl

        mView?.setFood(food)
        mView?.setSelectedUnit(food?.unit?.id)
    }

    fun setUnits(units: List<Unit>?) {
        mUnits = units

        mView?.setUnits(units)
        mView?.setSelectedUnit(mUnitId)
    }

    override fun getFood(id: Int) {
        mApiFoodRepository.getFoodFromCache(id)
                .subscribe(object : Subscriber<Food>() {
                    override fun onNext(t: Food?) {
                        setFood(t)
                    }

                    override fun onCompleted() {
                        getShopPlans(id)
                    }

                    override fun onError(e: Throwable?) {
                        mView?.showToast(e?.message)
                    }
                })

        mApiFoodRepository.getFood(id)
                .subscribe(object : Subscriber<Food>() {
                    override fun onNext(t: Food?) {
                        setFood(t)
                    }

                    override fun onCompleted() {
                        getShopPlans(id)
                    }

                    override fun onError(e: Throwable?) {
                        mView?.showToast(e?.message)
                    }
                })
    }

    override fun getUnits(boxId: Int) {
        mApiBoxRepository.getUnitsForBoxFromCache(boxId)
                .subscribe(object : Subscriber<List<Unit>>() {
                    override fun onNext(t: List<Unit>?) {
                        setUnits(t)
                    }

                    override fun onCompleted() {}

                    override fun onError(e: Throwable?) {
                        mView?.showToast(e?.message)
                    }

                })

        mApiBoxRepository.getUnitsForBox(boxId)
                .subscribe(object : Subscriber<List<Unit>>() {
                    override fun onNext(t: List<Unit>?) {
                        setUnits(t)
                    }

                    override fun onCompleted() {}

                    override fun onError(e: Throwable?) {
                        mView?.showToast(e?.message)
                    }
                })
    }

    override fun updateFood() {
        mId?.let { id ->
            mApiFoodRepository.updateFood(id, mName, mAmount, mExpirationDate, mImage, mBoxId, mUnitId)
                    .doOnSubscribe { mView?.showProgressBar() }
                    .doOnUnsubscribe { mView?.hideProgressBar() }
                    .subscribe(object : Subscriber<Food>() {
                        override fun onNext(t: Food?) {
                            mFood = t
                            mView?.onUpdateCompleted(t)
                        }

                        override fun onCompleted() {}

                        override fun onError(e: Throwable?) {
                            mView?.showToast(e?.message)
                        }
                    })
        }
    }

    override fun getShopPlans(id: Int) {
        mApiFoodRepository.getShopPlansForFoodFromCache(id)
                .subscribe(object : Subscriber<List<ShopPlan>>() {
                    override fun onNext(t: List<ShopPlan>?) {
                        mView?.setShopPlans(mFood, t?.filter { !it.done })
                    }

                    override fun onCompleted() {}

                    override fun onError(e: Throwable?) {
                        mView?.showToast(e?.message)
                    }
                })

        mApiFoodRepository.getShopPlansForFood(id)
                .subscribe(object : Subscriber<List<ShopPlan>>() {
                    override fun onNext(t: List<ShopPlan>?) {
                        mView?.setShopPlans(mFood, t?.filter { !it.done })
                    }

                    override fun onCompleted() {}

                    override fun onError(e: Throwable?) {
                        mView?.showToast(e?.message)
                    }
                })
    }

    override fun showCreateShopPlanDialog() {
        mView?.showCreateShopPlanDialog(mFood)
    }

    override fun createShopPlan(amount: Double, date: Date) {
        mFood?.let {
            mApiShopPlanRepository.createShopPlan(it.id, amount, date)
                    .doOnSubscribe { mView?.showProgressBar() }
                    .doOnUnsubscribe { mView?.hideProgressBar() }
                    .subscribe(object : Subscriber<ShopPlan>() {
                        override fun onNext(t: ShopPlan?) {}

                        override fun onCompleted() {
                            getShopPlans(it.id)
                        }

                        override fun onError(e: Throwable?) {
                            mView?.showToast(e?.message)
                        }
                    })
        }
    }

    override fun completeShopPlan(shopPlan: ShopPlan) {
        mApiShopPlanRepository.completeShopPlan(shopPlan.id)
                .doOnSubscribe { mView?.showProgressBar() }
                .doOnUnsubscribe { mView?.hideProgressBar() }
                .subscribe(object : Subscriber<ShopPlan>() {
                    override fun onNext(t: ShopPlan?) {
                        mView?.onCompletedCompleteShopPlan(t)
                    }

                    override fun onCompleted() {
                        mFood?.id?.let {
                            getFood(it)
                        }
                    }

                    override fun onError(e: Throwable?) {
                        mView?.showToast(e?.message)
                    }
                })
    }

    override fun isImageRegistered(): Boolean {
        return mImageUrl.isNullOrBlank()
    }

    override fun showImage() {
        mImageUrl?.let {
            mView?.showImageDialog(it)
        }
    }

    override fun selectUnit(id: Int) {
        mUnitId = id
        mView?.setSelectedUnit(id)
    }

    override fun editExpirationDate() {
        mView?.showEditDateDialog(mFood?.expirationDate)
    }

    override fun updateName(name: String) {
        mName = name
    }

    override fun updateAmount(amount: Double) {
        mAmount = amount
    }

    override fun updateNotice(notice: String) {
        mNotice = notice
    }

    override fun updateExpirationDate(date: Date) {
        mExpirationDate = date
        mView?.setExpirationDate(date)
    }

    override fun updateImage(image: Bitmap) {
        mImage = image
    }
}