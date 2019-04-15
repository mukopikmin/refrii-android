package com.refrii.client.foodlist

import com.refrii.client.data.api.models.Box
import com.refrii.client.data.api.models.Food
import com.refrii.client.data.api.source.ApiRepository
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
        // Temporally clear boxes set in drawer, for updating cache
        mView?.clearBoxes()

        mApiRepository.getBoxesFromCache()
                .subscribe({
                    mBoxes = it
                    mView?.setBoxes(mBoxes)
                }, {
                    mView?.showToast(it.message)

                    if (it is HttpException && it.code() == 401) {
                        mView?.signOut()
                    }
                })

        mApiRepository.getBoxes()
                .doOnSubscribe { mView?.showProgressBar() }
                .doOnUnsubscribe { mView?.hideProgressBar() }
                .subscribe({
                    mBoxes = it
                    mView?.setBoxes(it)
                    mView?.showToast("同期が完了しました")
                }, {
                    mView?.showToast(it.message)

                    if (it is HttpException && it.code() == 401) {
                        mView?.signOut()
                    }
                })
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
        mBox?.let {
            mApiRepository.updateFood(food.id, null, null, amount, null, null, null)
                    .doOnSubscribe { mView?.showProgressBar() }
                    .doOnUnsubscribe { mView?.hideProgressBar() }
                    .subscribe({
                        mView?.onFoodUpdated(it)
                        mView?.showSnackbar("${it.name} の数量が更新されました")
                    }, {
                        mView?.showToast(it.message)
                    })
        }
    }

    override fun confirmRemovingFood() {
        mView?.showConfirmDialog(mFood)
    }

    override fun removeFood() {
        mFood?.let {
            mApiRepository.removeFood(it.id)
                    .doOnSubscribe { mView?.showProgressBar() }
                    .doOnUnsubscribe { mView?.hideProgressBar() }
                    .subscribe({
                        mFood = null
                        mView?.onFoodUpdated(mFood)
                        mView?.showSnackbar("削除が完了しました")
                        getBoxes()
                    }, {
                        mView?.showToast(it.message)
                    })
        }
    }

    override fun createBox(name: String, notice: String) {
        mApiRepository.createBox(name, notice)
                .doOnSubscribe { mView?.showProgressBar() }
                .doOnUnsubscribe { mView?.hideProgressBar() }
                .subscribe({
                    mView?.showSnackbar("${it.name} が作成されました")
                    getBoxes()
                }, {
                    mView?.showToast(it.message)
                })
    }

    override fun showFood() {
        mFood?.let {
            mView?.showFood(it.id, mBox)
        }
    }

    override fun selectBox(box: Box) {
        mBox = box

        mApiRepository.getFoodsInBoxFromCache(box.id)
                .subscribe({
                    mFoods = it
                    mView?.setFoods(box.name, it)
                    mView?.setEmptyMessage(it)
                }, {
                    mView?.showToast(it.message)
                })

        mApiRepository.getFoodsInBox(box.id)
                .subscribe({
                    mFoods = it
                    mView?.setFoods(box.name, it)
                    mView?.setEmptyMessage(it)
                }, {
                    mView?.showToast(it.message)
                })
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
        mBox = null

        mApiRepository.getExpiringFoodsFromCache()
                .subscribe({
                    mFoods = it
                    mView?.setFoods("期限が1週間以内", it)
                }, {
                    mView?.showToast(it.message)
                })

        mApiRepository.getExpiringFoods()
                .subscribe({
                    mFoods = it
                    mView?.setFoods("期限が1週間以内", it)
                }, {
                    mView?.showToast(it.message)
                })
    }

    override fun registerPushToken(userId: Int, token: String) {
        mApiRepository.registerPushToken(userId, token)
                .subscribe({
                    mView?.savePushToken(token)
                    mView?.showToast(token)
                }, {
                    if (it is HttpException && it.code() == 401) {
                        mView?.signOut()
                    }
                })
    }

    override fun deleteLocalData() {
        mBox = null
        mView?.clearBoxes()
        mApiRepository.deleteLocalData()
    }
}