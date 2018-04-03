package com.refrii.client.foodlist

import com.refrii.client.data.api.models.Box
import com.refrii.client.data.api.models.Food
import com.refrii.client.data.api.source.ApiRepository
import com.refrii.client.data.api.source.ApiRepositoryCallback
import javax.inject.Inject

class FoodListPresenter
@Inject
constructor(private val mApiRepository: ApiRepository) : FoodListContract.Presenter {

    var mView: FoodListContract.View? = null
    private var mBoxes: List<Box>? = null
    var mBox: Box? = null

    override fun getBoxInfo() {
        mBox?.let {
            mView?.showBoxInfo(it)
        }
    }

    override fun pickBox(menuItemId: Int): Boolean {
        val box = mBoxes?.firstOrNull { menuItemId == it.id } ?: return false

        mBox = box
        box.foods?.let {
            mView?.setFoods(box, it)
        }

        return true
    }

    override fun getBoxes() {
        mView?.showProgressBar()

        mApiRepository.getBoxes(object : ApiRepositoryCallback<List<Box>> {
            override fun onNext(t: List<Box>?) {
                mBoxes = t
                t?.let {
                    mView?.setBoxes(it)
                }
            }

            override fun onCompleted() {
                mView?.hideProgressBar()
            }

            override fun onError(e: Throwable?) {
                e?.message?.let {
                    mView?.showToast(it)
                }
            }
        })
    }

    override fun incrementFood(food: Food) {
        food.increase(1.0)

        mView?.showProgressBar()

        mBox?.let {
            mApiRepository.updateFood(food, it, object : ApiRepositoryCallback<Food> {
                override fun onNext(t: Food?) {}

                override fun onCompleted() {
                    mView?.onFoodUpdated()
                    mView?.hideProgressBar()
                }

                override fun onError(e: Throwable?) {
                    e?.message?.let {
                        mView?.showToast(it)
                    }
                }
            })
        }
    }

    override fun decrementFood(food: Food) {
        food.decrease(1.0)

        mView?.showProgressBar()

        mBox?.let {
            mApiRepository.updateFood(food, it, object : ApiRepositoryCallback<Food> {
                override fun onNext(t: Food?) {}

                override fun onCompleted() {
                    mView?.onFoodUpdated()
                    mView?.hideProgressBar()
                }

                override fun onError(e: Throwable?) {
                    e?.message?.let {
                        mView?.showToast(it)
                    }
                }
            })
        }
    }

    override fun removeFood(id: Int) {
        mView?.showProgressBar()

        mApiRepository.removeFood(id, object : ApiRepositoryCallback<Void> {
            override fun onNext(t: Void?) {}

            override fun onCompleted() {
//                mView?.onFoodUpdated()
//                mView?.hideProgressBar()
                getBoxes()
            }

            override fun onError(e: Throwable?) {
                e?.message?.let {
                    mView?.showToast(it)
                }
            }
        })
    }
}