package com.refrii.client.food

import com.refrii.client.data.models.Box
import com.refrii.client.data.models.Food
import com.refrii.client.data.models.Unit
import com.refrii.client.data.source.ApiRepository
import rx.Subscriber
import java.util.*
import javax.inject.Inject

class FoodPresenter
@Inject
constructor(private val mApiRepository: ApiRepository) : FoodContract.Presenter {

    private var mView: FoodContract.View? = null
    private var mFood: Food? = null
    private var mBox: Box? = null
    private var mUnits: List<Unit>? = null
    private var mId: Int? = null
    private var mName: String? = null
    private var mAmount: Double? = null
    private var mNotice: String? = null
    private var mExpirationDate: Date? = null
    private var mBoxId: Int? = null
    private var mUnitId: Int? = null

    override fun takeView(view: FoodContract.View) {
        mView = view
    }

    fun setFood(food: Food?) {
        mFood = food
        mId = food?.id
        mName = food?.name
        mAmount = food?.amount
        mNotice = food?.notice
        mExpirationDate = food?.expirationDate
        mBoxId = food?.box?.id
        mUnitId = food?.unit?.id

        mView?.setFood(food)
        mView?.setSelectedUnit(food?.unit?.id)
    }

    fun setUnits(units: List<Unit>?) {
        mUnits = units

        mView?.setUnits(units)
        mView?.setSelectedUnit(mUnitId)
    }

    override fun getFood(id: Int) {
        mApiRepository.getFoodFromCache(id)
                .subscribe(object : Subscriber<Food>() {
                    override fun onNext(t: Food?) {
                        setFood(t)
                    }

                    override fun onCompleted() {}

                    override fun onError(e: Throwable?) {
                        mView?.showToast(e?.message)
                    }
                })

        mApiRepository.getFood(id)
                .subscribe(object : Subscriber<Food>() {
                    override fun onNext(t: Food?) {
                        setFood(t)
                    }

                    override fun onCompleted() {}

                    override fun onError(e: Throwable?) {
                        mView?.showToast(e?.message)
                    }
                })
    }

    override fun getUnits(boxId: Int) {
        mApiRepository.getUnitsForBoxFromCache(boxId)
                .subscribe(object : Subscriber<List<Unit>>() {
                    override fun onNext(t: List<Unit>?) {
                        setUnits(t)
                    }

                    override fun onCompleted() {}

                    override fun onError(e: Throwable?) {
                        mView?.showToast(e?.message)
                    }

                })

        mApiRepository.getUnitsForBox(boxId)
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
            mApiRepository.updateFood(id, mName, mNotice, mAmount, mExpirationDate, mBoxId, mUnitId)
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
}