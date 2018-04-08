package com.refrii.client.foodlist

import com.refrii.client.data.api.models.Box
import com.refrii.client.data.api.models.Food
import com.refrii.client.data.api.source.ApiRepository
import com.refrii.client.data.api.source.ApiRepositoryCallback
import javax.inject.Inject

class FoodListPresenter
@Inject
constructor(private val mApiRepository: ApiRepository) : FoodListContract.Presenter {

    private var mView: FoodListContract.View? = null
    private var mBoxes: List<Box>? = null
    private var mBox: Box? = null

    override fun takeView(view: FoodListContract.View) {
        mView = view
    }

    override fun getBoxInfo() {
        mBox?.let {
            mView?.showBoxInfo(it)
        }
    }

    override fun pickBox(menuItemId: Int): Boolean {
        val box = mBoxes?.firstOrNull { menuItemId == it.id } ?: return false

        mBox = box
        mView?.setFoods(box, box.foods)

        return true
    }

    override fun getBoxes() {
        mView?.showProgressBar()

        mApiRepository.getBoxes(object : ApiRepositoryCallback<List<Box>> {
            override fun onNext(t: List<Box>?) {
                mBoxes = t
                mView?.setBoxes(t)
            }

            override fun onCompleted() {
                mView?.hideProgressBar()
            }

            override fun onError(e: Throwable?) {
                mView?.showToast(e?.message)
            }
        })
    }

    override fun incrementFood(food: Food) {
        food.increase(1.0)

        mView?.showProgressBar()

        mBox?.let {
            mApiRepository.updateFood(food, it, object : ApiRepositoryCallback<Food> {
                override fun onNext(t: Food?) {
                    mView?.showSnackbar("Amount of ${t?.name} is incremented")
                }

                override fun onCompleted() {
                    mView?.onFoodUpdated()
                    mView?.hideProgressBar()
                }

                override fun onError(e: Throwable?) {
                    mView?.showToast(e?.message)
                }
            })
        }
    }

    override fun decrementFood(food: Food) {
        food.decrease(1.0)

        mView?.showProgressBar()

        mBox?.let {
            mApiRepository.updateFood(food, it, object : ApiRepositoryCallback<Food> {
                override fun onNext(t: Food?) {
                    mView?.showSnackbar("Amount of ${t?.name} is decremented")
                }

                override fun onCompleted() {
                    mView?.onFoodUpdated()
                    mView?.hideProgressBar()
                }

                override fun onError(e: Throwable?) {
                    mView?.showToast(e?.message)
                }
            })
        }
    }

    override fun removeFood(id: Int) {
        mView?.showProgressBar()

        mApiRepository.removeFood(id, object : ApiRepositoryCallback<Void> {
            override fun onNext(t: Void?) {
                mView?.showSnackbar("Food is removed successfully")
            }

            override fun onCompleted() {
                mView?.onFoodUpdated()
                mView?.hideProgressBar()
                getBoxes()
            }

            override fun onError(e: Throwable?) {
                mView?.showToast(e?.message)
            }
        })
    }

    override fun showFood(id: Int) {
        mView?.showFood(id, mBox)
    }

    override fun selectBox(box: Box) {
        mBox = box
        mView?.setFoods(box, box.foods)
    }

    override fun addFood() {
        mView?.addFood(mBox)
    }
}