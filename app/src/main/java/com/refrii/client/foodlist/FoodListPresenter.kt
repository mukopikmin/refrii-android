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
    private var mBox: Box? = null
    private var mFoods: List<Food>? = null
    private var mFood: Food? = null

    override fun takeView(view: FoodListContract.View) {
        mView = view
    }

    override fun getBox(): Box? {
        return mBox
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

    override fun incrementFood() {
        mFood?.let {
            val step: Double = it.unit?.step ?: 0.toDouble()
            val amount = it.amount + step

            updateFood(it, amount)
        }
    }

    override fun decrementFood() {
        mFood?.let {
            val step = it.unit?.step ?: 0.toDouble()
            val amount = it.amount - step

            if (amount < 0) {
                updateFood(it, 0.toDouble())
            } else {
                updateFood(it, amount)
            }
        }
    }

    private fun updateFood(food: Food, amount: Double = 0.toDouble()) {
        mView?.showProgressBar()

        mBox?.let {
            mApiRepository.updateFood(object : ApiRepositoryCallback<Food> {
                override fun onNext(t: Food?) {
                    mView?.onFoodUpdated(t)
                    mView?.showSnackbar("${t?.name} の数量が更新されました")
                }

                override fun onCompleted() {
                    mView?.hideProgressBar()
                }

                override fun onError(e: Throwable?) {
                    mView?.showToast(e?.message)
                    mView?.hideProgressBar()
                }
            }, food.id, null, null, amount, null, null, null)
        }
    }

    override fun confirmRemovingFood() {
        mView?.showConfirmDialog(mFood)
    }

    override fun removeFood() {
        mFood?.let {
            mView?.showProgressBar()

            mApiRepository.removeFood(it.id, object : ApiRepositoryCallback<Void> {
                override fun onNext(t: Void?) {
                    mFood = null
                    mView?.onFoodUpdated(mFood)
                }

                override fun onCompleted() {
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
    }

    override fun createBox(name: String, notice: String) {
        mView?.showProgressBar()

        mApiRepository.createBox(object : ApiRepositoryCallback<Box> {
            override fun onNext(t: Box?) {
                mBoxes = mApiRepository.getBoxes(object : ApiRepositoryCallback<List<Box>> {
                    override fun onNext(t: List<Box>?) {}
                    override fun onCompleted() {}
                    override fun onError(e: Throwable?) {}
                })

                t?.let {
                    selectBox(it)
                }
            }

            override fun onCompleted() {
                mView?.hideProgressBar()
            }

            override fun onError(e: Throwable?) {
                mView?.hideProgressBar()
            }
        }, name, notice)
    }

    override fun showFood() {
        mFood?.let {
            mView?.showFood(it.id, mBox)
        }
    }

    override fun selectBox(box: Box) {
        mBox = box

        mFoods = mApiRepository.getFoods(box, object : ApiRepositoryCallback<List<Food>> {
            override fun onNext(t: List<Food>?) {
                mFoods = t
                mView?.setFoods(box.name, t)
                mView?.setEmptyMessage(t)
            }

            override fun onCompleted() {
                mView?.hideProgressBar()
            }

            override fun onError(e: Throwable?) {
                mView?.showToast(e?.message)
            }
        })

        mView?.setFoods(box.name, mFoods)
        mView?.hideBottomNavigation()
        mView?.setEmptyMessage(mFoods)
    }

    override fun selectFood(food: Food) {
        if (mFood == null) {
            mFood = food
            mView?.showBottomNavigation(food)
        } else {
            mFood?.let {
                if (it.id == food.id) {
                    mFood = null
                    mView?.hideBottomNavigation()
                } else {
                    mFood = food
                    mView?.showBottomNavigation(food)
                }
            }
        }
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