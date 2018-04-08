package com.refrii.client.newfood

import com.refrii.client.data.api.models.Box
import com.refrii.client.data.api.models.Food
import com.refrii.client.data.api.models.Unit
import com.refrii.client.data.api.source.ApiRepository
import com.refrii.client.data.api.source.ApiRepositoryCallback
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

        mBox?.let {
            mView?.showProgressBar()

            mApiRepository.createFood(name, notice, amount, it, unit, expirationDate, object : ApiRepositoryCallback<Food> {
                override fun onNext(t: Food?) {
                    mFood = t
                }

                override fun onCompleted() {
                    mView?.createCompleted(mFood)
                }

                override fun onError(e: Throwable?) {
                    mView?.showToast(e?.message)
                }
            })
        }
    }

    override fun getUnits(userId: Int) {
        mView?.showProgressBar()

        mApiRepository.getUnits(userId, object : ApiRepositoryCallback<List<Unit>> {
            override fun onNext(t: List<Unit>?) {
                mUnits = t
                mView?.setUnits(t)
            }

            override fun onCompleted() {
                mView?.hideProgressBar()
            }

            override fun onError(e: Throwable?) {
                mView?.showToast(e?.message)
            }
        })
    }

    override fun getBox(id: Int) {
        mView?.showProgressBar()

        mApiRepository.getBox(id, object : ApiRepositoryCallback<Box> {
            override fun onNext(t: Box?) {
                mBox = t
            }

            override fun onCompleted() {
                mView?.hideProgressBar()
            }

            override fun onError(e: Throwable?) {
                mView?.showToast(e?.message)
            }
        })
    }

    override fun pickUnit(label: String): Unit? {
        return mUnits?.firstOrNull { it.label == label }
    }
}