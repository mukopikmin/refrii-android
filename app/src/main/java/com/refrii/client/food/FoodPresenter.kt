package com.refrii.client.food

import com.refrii.client.data.api.models.Box
import com.refrii.client.data.api.models.Food
import com.refrii.client.data.api.models.Unit
import com.refrii.client.data.api.source.ApiRepository
import com.refrii.client.data.api.source.ApiRepositoryCallback
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
        mFood = mApiRepository.getFood(id, object : ApiRepositoryCallback<Food> {
            override fun onNext(t: Food?) {
                mFood = t
                mId = t?.id
                mName = t?.name
                mAmount = t?.amount
                mNotice = t?.notice
                mExpirationDate = t?.expirationDate
                mBoxId = t?.box?.id
                mUnitId = t?.unit?.id

                mView?.setFood(t)
            }

            override fun onCompleted() {
//                mBoxId?.let {
//                    getBox(id)
//                }
            }

            override fun onError(e: Throwable?) {
                mView?.showToast(e?.message)
            }
        })

        mView?.setFood(mFood)
    }

    override fun getUnits(boxId: Int) {
        mUnits = mApiRepository.getUnitsForBox(boxId, object : ApiRepositoryCallback<List<Unit>> {
            override fun onNext(t: List<Unit>?) {
                mUnits = t
                mView?.setUnits(t)
            }

            override fun onCompleted() {}

            override fun onError(e: Throwable?) {
                mView?.showToast(e?.message)
            }
        })

        mView?.setUnits(mUnits)
    }

    override fun updateFood() {

        mId?.let {
            mView?.showProgressBar()

            mApiRepository.updateFood(object : ApiRepositoryCallback<Food> {
                override fun onNext(t: Food?) {
                    mFood = t
                }

                override fun onCompleted() {
                    mView?.hideProgressBar()
                    mView?.onUpdateCompleted()
                }

                override fun onError(e: Throwable?) {
                    mView?.showToast(e?.message)
                }
            }, it, mName, mNotice, mAmount, mExpirationDate, mBoxId, mUnitId)
        }
    }

    override fun selectUnit(id: Int) {
        mUnitId = id
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