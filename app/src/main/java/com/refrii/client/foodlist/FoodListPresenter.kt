package com.refrii.client.foodlist

import com.refrii.client.data.api.models.Box
import com.refrii.client.data.api.models.Food
import com.refrii.client.data.api.source.ApiRepository
import com.refrii.client.data.api.source.ApiRepositoryCallback
import retrofit2.HttpException
import javax.inject.Inject

class FoodListPresenter
@Inject
constructor(private val mApiRepository: ApiRepository) : FoodListContract.Presenter {

    private var mView: FoodListContract.View? = null
    private var mBoxes: List<Box>? = null
    var mBox: Box? = null
    private var mFoods: List<Food>? = null

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

        selectBox(box)

        return true
    }

    override fun getBoxes() {
        // Temporally clear boxes shown in drawer, for updating cache
        mView?.clearBoxes()

        mBoxes = mApiRepository.getBoxes(object : ApiRepositoryCallback<List<Box>> {
            override fun onNext(t: List<Box>?) {
                mBoxes = t
                mView?.setBoxes(t)
            }

            override fun onCompleted() {
                mView?.showToast("同期が完了しました")
            }

            override fun onError(e: Throwable?) {
                mView?.showToast(e?.message)

                if (e is HttpException && e.code() == 401) {
                    mView?.signOut()
                }
            }
        })

        mView?.setBoxes(mBoxes)

    }

    override fun incrementFood(food: Food) {
        val step: Double = food.unit?.step ?: 0.toDouble()
        val amount = food.amount + step

        updateFood(food, amount)
    }

    override fun decrementFood(food: Food) {
        val step = food.unit?.step ?: 0.toDouble()
        val amount = food.amount - step

        if (amount < 0) {
            updateFood(food, 0.toDouble())
        } else {
            updateFood(food, amount)
        }
    }

    private fun updateFood(food: Food, amount: Double = 0.toDouble()) {
        mView?.showProgressBar()

        mBox?.let {
            mApiRepository.updateFood(object : ApiRepositoryCallback<Food> {
                override fun onNext(t: Food?) {
                    mView?.showSnackbar("${t?.name} の数量が更新されました")
                }

                override fun onCompleted() {
                    mView?.onFoodUpdated()
                    mView?.hideProgressBar()
                }

                override fun onError(e: Throwable?) {
                    mView?.showToast(e?.message)
                    mView?.hideProgressBar()
                }
            }, food.id, it.id, amount = amount)
        }
    }

    override fun removeFood(id: Int) {
        mView?.showProgressBar()

        mApiRepository.removeFood(id, object : ApiRepositoryCallback<Void> {
            override fun onNext(t: Void?) {}

            override fun onCompleted() {
                mView?.onFoodUpdated()
                mView?.hideProgressBar()
                mView?.showSnackbar("削除が完了しました")
                getBoxes()
            }

            override fun onError(e: Throwable?) {
                mView?.showToast(e?.message)
                mView?.hideProgressBar()
            }
        })
    }

    override fun showFood(id: Int) {
        mView?.showFood(id, mBox)
    }

    override fun selectBox(box: Box) {
        mBox = box

        mFoods = mApiRepository.getFoods(box, object : ApiRepositoryCallback<List<Food>> {
            override fun onNext(t: List<Food>?) {
                mFoods = t
                mView?.setFoods(box.name, t)
            }

            override fun onCompleted() {
                mView?.hideProgressBar()
            }

            override fun onError(e: Throwable?) {
                mView?.showToast(e?.message)
            }
        })

        mView?.setFoods(box.name, mFoods)
    }

    override fun addFood() {
        mView?.addFood(mBox)
    }

    override fun getExpiringFoods() {
        mFoods = mApiRepository.getExpiringFoods(object : ApiRepositoryCallback<List<Food>> {
            override fun onNext(t: List<Food>?) {
                mFoods = t
                mView?.setFoods("期限が1週間以内", mFoods)
            }

            override fun onCompleted() {}

            override fun onError(e: Throwable?) {
                mView?.showToast(e?.message)
            }
        })

        mBox = null
        mView?.setFoods("期限が1週間以内", mFoods)
    }

    override fun deleteLocalData() {
        mBox = null
        mView?.clearBoxes()
        mApiRepository.deleteLocalData()
    }
}