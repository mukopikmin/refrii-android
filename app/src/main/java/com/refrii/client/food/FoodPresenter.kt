package com.refrii.client.food

import com.refrii.client.data.api.models.Box
import com.refrii.client.data.api.models.Food
import com.refrii.client.data.api.models.Unit
import com.refrii.client.data.api.source.ApiRepository
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

    override fun getFood(id: Int) {
        mApiRepository.getFood(id)
                .subscribe({
                    mFood = it
                    mId = it?.id
                    mName = it?.name
                    mAmount = it?.amount
                    mNotice = it?.notice
                    mExpirationDate = it?.expirationDate
                    mBoxId = it?.box?.id
                    mUnitId = it?.unit?.id

                    mView?.setFood(it)
                    mView?.setSelectedUnit(mUnitId)
                }, {
                    mView?.showToast(it.message)
                })
    }

    override fun getUnits(boxId: Int) {
        mApiRepository.getUnitsForBoxFromCache(boxId)
                .subscribe({
                    mUnits = it
                    mView?.setUnits(it)
                    mView?.setSelectedUnit(mUnitId)
                }, {
                    mView?.showToast(it.message)
                })

        mApiRepository.getUnitsForBox(boxId)
                .subscribe({
                    mUnits = it
                    mView?.setUnits(it)
                    mView?.setSelectedUnit(mUnitId)
                }, {
                    mView?.showToast(it.message)
                })
    }

    override fun updateFood() {
        mId?.let { id ->
            mApiRepository.updateFood(id, mName, mNotice, mAmount, mExpirationDate, mBoxId, mUnitId)
                    .doOnSubscribe { mView?.showProgressBar() }
                    .doOnUnsubscribe { mView?.hideProgressBar() }
                    .subscribe({
                        mFood = it
                        mView?.onUpdateCompleted()
                    }, {
                        mView?.showToast(it.message)
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
        this.mName = name
    }

    override fun updateAmount(amount: Double) {
        this.mAmount = amount
    }

    override fun updateNotice(notice: String) {
        this.mNotice = notice
    }

    override fun updateExpirationDate(date: Date) {
        this.mExpirationDate = date
        mView?.setExpirationDate(date)
    }
}