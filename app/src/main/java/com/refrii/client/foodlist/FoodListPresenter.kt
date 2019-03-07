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

        mBox = box
        mView?.showProgressBar()

        mApiRepository.getFoodsInBox(box, object : ApiRepositoryCallback<List<Food>> {
            override fun onNext(t: List<Food>?) {
                mFoods = t
                mView?.setFoods(box, t)
            }

            override fun onCompleted() {
                mView?.hideProgressBar()
            }

            override fun onError(e: Throwable?) {
                mView?.showToast(e?.message)
            }

        })

        return true
    }

    override fun getBoxes() {
//        mView?.showProgressBar()

        mBoxes = mApiRepository.getBoxes(object : ApiRepositoryCallback<List<Box>> {
            override fun onNext(t: List<Box>?) {
                mBoxes = t
                mView?.setBoxes(t)
            }

            override fun onCompleted() {
                mView?.hideProgressBar()
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
        food.increase(1.0)

//        mView?.showProgressBar()

        mBox?.let {
            mApiRepository.updateFood(food, it, object : ApiRepositoryCallback<Food> {
                override fun onNext(t: Food?) {
                    mView?.showSnackbar("${t?.name} の数量が更新されました")
                }

                override fun onCompleted() {
                    mView?.onFoodUpdated()
                    mView?.showToast("同期が完了しました")
//                    mView?.hideProgressBar()
                }

                override fun onError(e: Throwable?) {
                    mView?.showToast(e?.message)
                }
            })
        }
    }

    override fun decrementFood(food: Food) {
        food.decrease(1.0)

//        mView?.showProgressBar()

        mBox?.let {
            mApiRepository.updateFood(food, it, object : ApiRepositoryCallback<Food> {
                override fun onNext(t: Food?) {
                    mView?.showSnackbar("${t?.name} の数量が更新されました")
                }

                override fun onCompleted() {
                    mView?.onFoodUpdated()
                    mView?.showToast("同期が完了しました")
//                    mView?.hideProgressBar()
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
//                mView?.showSnackbar("削除が完了しました")
            }

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

        mFoods = mApiRepository.getFoodsInBox(box, object : ApiRepositoryCallback<List<Food>> {
            override fun onNext(t: List<Food>?) {
                mFoods = t
                mView?.setFoods(box, t)
            }

            override fun onCompleted() {
                mView?.hideProgressBar()
            }

            override fun onError(e: Throwable?) {
                mView?.showToast(e?.message)
            }

        })

        mView?.setFoods(box, mFoods)
    }

    override fun addFood() {
        mView?.addFood(mBox)
    }
}