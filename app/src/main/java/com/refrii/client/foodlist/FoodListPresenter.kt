package com.refrii.client.foodlist

import com.refrii.client.data.models.Box
import com.refrii.client.data.models.Food
import com.refrii.client.data.models.User
import com.refrii.client.data.source.ApiRepository
import retrofit2.HttpException
import rx.Subscriber
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

    fun setBoxes(boxes: List<Box>?) {
        mBoxes = boxes

        mView?.setBoxes(boxes)
    }

    fun setBox(box: Box?, foods: List<Food>? = null) {
        mBox = box
        mFoods = foods

        mView?.setFoods(box?.name, foods)
        mView?.setEmptyMessage(foods)
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
                .subscribe(object : Subscriber<List<Box>>() {
                    override fun onNext(t: List<Box>?) {
                        setBoxes(t)
                    }

                    override fun onCompleted() {}

                    override fun onError(e: Throwable?) {
                        mView?.showToast(e?.message)
                    }
                })

        mApiRepository.getBoxes()
                .doOnSubscribe { mView?.showProgressBar() }
                .doOnUnsubscribe { mView?.hideProgressBar() }
                .subscribe(object : Subscriber<List<Box>>() {
                    override fun onNext(t: List<Box>?) {
                        setBoxes(t)
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

    fun updateFood(food: Food, amount: Double = 0.toDouble()) {
        mApiRepository.updateFood(food.id, null, null, amount, null, null, null)
                .doOnSubscribe { mView?.showProgressBar() }
                .doOnUnsubscribe { mView?.hideProgressBar() }
                .subscribe(object : Subscriber<Food>() {
                    override fun onNext(t: Food?) {
                        mView?.onFoodUpdated(t)
                        mView?.showSnackbar("${t?.name} の数量が更新されました")
                    }

                    override fun onCompleted() {}

                    override fun onError(e: Throwable?) {
                        mView?.showToast(e?.message)
                    }
                })
    }

    override fun confirmRemovingFood() {
        mView?.showConfirmDialog(mFood)
    }

    override fun removeFood() {
        mFood?.let {
            mApiRepository.removeFood(it.id)
                    .doOnSubscribe { mView?.showProgressBar() }
                    .doOnUnsubscribe { mView?.hideProgressBar() }
                    .subscribe(object : Subscriber<Void>() {
                        override fun onNext(t: Void?) {}

                        override fun onCompleted() {
                            mFood = null

                            mView?.onFoodUpdated(it)
                            mView?.showSnackbar("削除が完了しました")
                            getBoxes()
                        }

                        override fun onError(e: Throwable?) {
                            mView?.showToast(e?.message)
                        }
                    })
        }
    }

    override fun createBox(name: String, notice: String) {
        mApiRepository.createBox(name, notice)
                .doOnSubscribe { mView?.showProgressBar() }
                .doOnUnsubscribe { mView?.hideProgressBar() }
                .subscribe(object : Subscriber<Box>() {
                    override fun onNext(t: Box?) {
                        mView?.showSnackbar("${t?.name} が作成されました")
                    }

                    override fun onCompleted() {
                        getBoxes()
                    }

                    override fun onError(e: Throwable?) {
                        mView?.showToast(e?.message)
                    }
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
                .subscribe(object : Subscriber<List<Food>>() {
                    override fun onNext(t: List<Food>?) {
                        setBox(box, t)
                    }

                    override fun onCompleted() {}

                    override fun onError(e: Throwable?) {
                        mView?.showToast(e?.message)
                    }
                })

        mApiRepository.getFoodsInBox(box.id)
                .subscribe(object : Subscriber<List<Food>>() {
                    override fun onNext(t: List<Food>?) {
                        setBox(box, t)
                    }

                    override fun onCompleted() {}

                    override fun onError(e: Throwable?) {
                        mView?.showToast(e?.message)
                    }
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
                .subscribe(object : Subscriber<List<Food>>() {
                    override fun onNext(t: List<Food>?) {
                        mFoods = t
                        mView?.setFoods("期限が1週間以内", t)
                    }

                    override fun onCompleted() {}

                    override fun onError(e: Throwable?) {
                        mView?.showToast(e?.message)
                    }
                })

        mApiRepository.getExpiringFoods()
                .subscribe(object : Subscriber<List<Food>>() {
                    override fun onNext(t: List<Food>?) {
                        mFoods = t
                        mView?.setFoods("期限が1週間以内", t)
                    }

                    override fun onCompleted() {}

                    override fun onError(e: Throwable?) {
                        mView?.showToast(e?.message)
                    }
                })
    }

    override fun registerPushToken(userId: Int, token: String) {
        mApiRepository.registerPushToken(userId, token)
                .subscribe(object : Subscriber<User>() {
                    override fun onNext(t: User?) {}

                    override fun onCompleted() {
                        mView?.savePushToken(token)
                        mView?.showToast(token)
                    }

                    override fun onError(e: Throwable?) {
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