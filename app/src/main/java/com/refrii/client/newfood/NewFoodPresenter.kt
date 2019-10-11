package com.refrii.client.newfood

import com.refrii.client.data.models.Box
import com.refrii.client.data.models.Food
import com.refrii.client.data.models.Unit
import com.refrii.client.data.source.ApiBoxRepository
import com.refrii.client.data.source.ApiFoodRepository
import rx.Subscriber
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
            mApiFoodRepository.createFood(name, amount, box, unit, expirationDate)
                    .doOnSubscribe { mView?.showProgressBar() }
                    .doOnUnsubscribe { mView?.hideProgressBar() }
                    .subscribe(object : Subscriber<Food>() {
                        override fun onNext(t: Food?) {
                            mFood = t
                            mView?.createCompleted(t)
                        }

                        override fun onCompleted() {}

                        override fun onError(e: Throwable?) {
                            mView?.showToast(e?.message)
                        }
                    })
        }
    }

    override fun getUnits(boxId: Int) {
        mApiBoxRepository.getUnitsForBoxFromCache(boxId)
                .subscribe(object : Subscriber<List<Unit>>() {
                    override fun onNext(t: List<Unit>?) {
                        mUnits = t
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
                        mUnits = t
                        mView?.setUnits(t)

                        if (t.isNullOrEmpty()) {
                            mView?.goToAddUnit()
                        }
                    }

                    override fun onCompleted() {}

                    override fun onError(e: Throwable?) {
                        mView?.showToast(e?.message)
                    }
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