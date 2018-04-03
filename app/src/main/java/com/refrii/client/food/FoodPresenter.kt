package com.refrii.client.food

import com.refrii.client.data.api.models.Box
import com.refrii.client.data.api.models.Food
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

    override fun takeView(view: FoodContract.View) {
        mView = view
    }

    override fun getFood(id: Int) {
        mView?.showProgressBar()

        mApiRepository.getFood(id, object : ApiRepositoryCallback<Food> {
            override fun onNext(t: Food?) {
                mFood = t
                mView?.setFood(t, mBox)
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
                mView?.setFood(mFood, t)
            }

            override fun onCompleted() {
                mView?.hideProgressBar()
            }

            override fun onError(e: Throwable?) {
                mView?.showToast(e?.message)
            }
        })
    }

    override fun updateFood() {
        mView?.showProgressBar()

        mFood?.let { food ->
            mBox?.let { box ->
                mApiRepository.updateFood(food, box, object : ApiRepositoryCallback<Food> {
                    override fun onNext(t: Food?) {
                        mFood = t
                    }

                    override fun onCompleted() {
                        mView?.hideProgressBar()
                        mView?.onBeforeEdit()
                        mView?.showSnackbar("Update completed")
                    }

                    override fun onError(e: Throwable?) {
                        mView?.showToast(e?.message)
                    }
                })
            }
        }
    }

    override fun editName() {
        mView?.showEditNameDialog(mFood?.name)
    }

    override fun editAmount() {
        mView?.showEditAmountDialog(mFood?.amount)
    }

    override fun editNotice() {
        mView?.showEditNoticeDialog(mFood?.notice)
    }

    override fun editExpirationDate() {
        mView?.showEditDateDialog(mFood?.expirationDate)
    }

    override fun updateName(name: String) {
        mFood?.name = name
        mView?.setFood(mFood, mBox)
    }

    override fun updateAmount(amount: Double) {
        mFood?.amount = amount
        mView?.setFood(mFood, mBox)
    }

    override fun updateNotice(notice: String) {
        mFood?.notice = notice
        mView?.setFood(mFood, mBox)
    }

    override fun updateExpirationDate(date: Date) {
        mFood?.expirationDate = date
        mView?.setFood(mFood, mBox)
    }
}