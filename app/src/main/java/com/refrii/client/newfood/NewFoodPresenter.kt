package com.refrii.client.newfood

import com.refrii.client.data.models.Box
import com.refrii.client.data.models.Food
import com.refrii.client.data.models.Unit
import com.refrii.client.data.source.ApiBoxRepository
import com.refrii.client.data.source.ApiFoodRepository
import java.util.*
import javax.inject.Inject

class NewFoodPresenter
@Inject
constructor(
        private val mApiBoxRepository: ApiBoxRepository,
        private val mApiFoodRepository: ApiFoodRepository) : NewFoodContract.Presenter {

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
            mApiFoodRepository.createFood(name, notice, amount, box, unit, expirationDate)
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
        mApiBoxRepository.getUnitsForBoxFromCache(boxId)
                .subscribe({
                    mUnits = it
                    mView?.setUnits(it)
                }, {
                    mView?.showToast(it.message)
                })

        mApiBoxRepository.getUnitsForBox(boxId)
                .subscribe({
                    mUnits = it
                    mView?.setUnits(it)
                }, {
                    mView?.showToast(it.message)
                })
    }

    override fun getBox(id: Int) {
        mApiBoxRepository.getBoxFromCache(id)
                .subscribe({
                    mBox = it
                    mView?.setBox(it)
                }, {
                    mView?.showToast(it.message)
                })

        mApiBoxRepository.getBox(id)
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