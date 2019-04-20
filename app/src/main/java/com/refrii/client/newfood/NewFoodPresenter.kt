package com.refrii.client.newfood

import com.refrii.client.data.models.Box
import com.refrii.client.data.models.Food
import com.refrii.client.data.models.Unit
import com.refrii.client.data.source.ApiRepository
import java.util.*
import javax.inject.Inject

class NewFoodPresenter
@Inject
constructor(private val mApiRepository: ApiRepository) : NewFoodContract.Presenter {

    private var mView: NewFoodContract.View? = null
    private var mUnits: List<Unit>? = null
    private var mBox: Box? = null
    private var mFood: Food? = null

    override fun takeView(view: NewFoodContract.View) {
        mView = view
    }

    override fun createFood(name: String, notice: String, amount: Double, unit: Unit?, expirationDate: Date) {
        unit ?: return

        mBox?.let { box ->
            mApiRepository.createFood(name, notice, amount, box, unit, expirationDate)
                    .doOnSubscribe { mView?.showProgressBar() }
                    .doOnUnsubscribe { mView?.hideProgressBar() }
                    .subscribe({
                        mFood = it
                        mView?.createCompleted(mFood)
                    }, {
                        mView?.showToast(it.message)
                    })
        }
    }

    override fun getUnits(boxId: Int) {
        mApiRepository.getUnitsForBoxFromCache(boxId)
                .subscribe({
                    mUnits = it
                    mView?.setUnits(it)
                }, {
                    mView?.showToast(it.message)
                })

        mApiRepository.getUnitsForBox(boxId)
                .subscribe({
                    mUnits = it
                    mView?.setUnits(it)
                }, {
                    mView?.showToast(it.message)
                })
    }

    override fun getBox(id: Int) {
        mApiRepository.getBoxFromCache(id)
                .subscribe({
                    mBox = it
                    mView?.setBox(it)
                }, {
                    mView?.showToast(it.message)
                })

        mApiRepository.getBox(id)
                .subscribe({
                    mBox = it
                    mView?.setBox(it)
                }, {
                    mView?.showToast(it.message)
                })
    }

    override fun pickUnit(label: String): Unit? {
        return mUnits?.firstOrNull { it.label == label }
    }
}