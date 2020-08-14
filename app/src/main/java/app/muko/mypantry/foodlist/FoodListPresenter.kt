package app.muko.mypantry.foodlist

import androidx.lifecycle.LiveData
import app.muko.mypantry.data.dao.LocalDatabase
import app.muko.mypantry.data.models.Box
import app.muko.mypantry.data.models.Food
import app.muko.mypantry.data.models.User
import app.muko.mypantry.data.source.ApiBoxRepository
import app.muko.mypantry.data.source.ApiFoodRepository
import app.muko.mypantry.data.source.ApiUserRepository
import io.reactivex.CompletableObserver
import io.reactivex.disposables.Disposable
import io.reactivex.subscribers.DisposableSubscriber
import retrofit2.HttpException
import javax.inject.Inject

class FoodListPresenter
@Inject
constructor(
        private val mLocalDatabase: LocalDatabase,
        private val mApiBoxRepository: ApiBoxRepository,
        private val mApiFoodRepository: ApiFoodRepository,
        private val mApiUserRepository: ApiUserRepository
) : FoodListContract.Presenter {

    private var mView: FoodListContract.View? = null
    var mBox: Box? = null
    private var mFoods: List<Food>? = null
    private var mFood: Food? = null

    lateinit var mBoxesLiveData: LiveData<List<Box>>
    lateinit var mFoodsLiveData: LiveData<List<Food>>

    override fun takeView(view: FoodListContract.View) {
        mView = view
        mBoxesLiveData = mLocalDatabase.boxDao().getAllLiveData()
        mFoodsLiveData = mLocalDatabase.foodDao().getAllLiveData()
    }

    fun setBox(box: Box?, foods: List<Food>? = null) {
        box ?: return

        mBox = box
        mFoods = mFoodsLiveData.value?.filter { it.box.id == box.id }

        mView?.setFoods(box.name, foods)
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
        val box = mBoxesLiveData.value?.firstOrNull { menuItemId == it.id } ?: return false

        selectBox(box)

        return true
    }

    override fun getBoxes() {
        // Temporally clear boxes set in drawer, for updating cache
        mView?.clearBoxes()

        mApiBoxRepository.getAll()
                .doOnSubscribe { mView?.showProgressBar() }
                .doFinally { mView?.hideProgressBar() }
                .subscribe(object : DisposableSubscriber<List<Box>>() {
                    override fun onNext(t: List<Box>) {
                        if (t.isNullOrEmpty()) {
                            mView?.setNoBoxesMessage()
                        }
                    }

                    override fun onComplete() {}

                    override fun onError(e: Throwable) {
                        if (e is HttpException) {
                            mView?.showToast(e.response()?.message())
                        } else {
                            mView?.showToast(e.message)
                        }
                    }

                })
    }

    override fun incrementFood() {
        mFood?.let {
            val step: Double = it.unit.step

            it.amount = it.amount + step
            updateFood(it)
        }
    }

    override fun decrementFood() {
        mFood?.let {
            val step = it.unit.step ?: 0.toDouble()
            val amount = it.amount - step

            if (amount < 0) {
                it.amount = 0.0
            } else {
                it.amount = amount
            }

            updateFood(it)
        }
    }

    private fun updateFood(food: Food) {
        mApiFoodRepository.update(food)
                .doOnSubscribe { mView?.showProgressBar() }
                .doFinally { mView?.hideProgressBar() }
                .subscribe(object : CompletableObserver {
                    override fun onComplete() {
                        mView?.showSnackbar("${food.name} の数量が更新されました")
                    }

                    override fun onSubscribe(d: Disposable) {}

                    override fun onError(e: Throwable) {
                        mView?.showToast(e.message)
                    }
                })
    }

    override fun confirmRemovingFood() {
        mView?.showConfirmDialog(mFood)
    }

    override fun removeFood() {
        mFood?.let {
            mApiFoodRepository.remove(it)
                    .doOnSubscribe { mView?.showProgressBar() }
                    .doFinally { mView?.hideProgressBar() }
                    .subscribe(object : CompletableObserver {
                        override fun onComplete() {
                            mFood = null

                            mView?.onFoodUpdated(it)
                            mView?.showSnackbar("削除が完了しました")
                            getBoxes()
                        }

                        override fun onSubscribe(d: Disposable) {}

                        override fun onError(e: Throwable) {
                            mView?.showToast(e.message)
                        }
                    })
        }
    }

    override fun createBox(name: String, notice: String) {
        val box = Box.temp(name, notice)

        mApiBoxRepository.create(box)
                .doOnSubscribe { mView?.showProgressBar() }
                .doFinally { mView?.hideProgressBar() }
                .subscribe(object : CompletableObserver {
                    override fun onComplete() {
                        mView?.onBoxCreated()
                        mView?.showSnackbar("${box.name} が作成されました")
                        getBoxes()
                    }

                    override fun onSubscribe(d: Disposable) {}

                    override fun onError(e: Throwable) {
                        mView?.showToast(e.message)
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
        setBox(box)

        mApiFoodRepository.getByBox(box.id)
                .doOnSubscribe { mView?.showProgressBar() }
                .doFinally { mView?.hideProgressBar() }
                .subscribe(object : DisposableSubscriber<List<Food>>() {
                    override fun onNext(t: List<Food>?) {
                        mBox?.let {
                            if (it.id == box.id) {
                                setBox(box, t)

                                mView?.setEmptyMessage(t)
                            }
                        }
                    }

                    override fun onComplete() {}

                    override fun onError(e: Throwable?) {
                        mView?.showToast(e?.message)
                    }
                })
    }


    override fun selectFood(food: Food) {
        mFood = food
        mView?.showBottomNavigation(food)
    }

    override fun isFoodSelected(food: Food): Boolean {
        mFood?.let {
            return it.id == food.id
        }

        return false
    }

    override fun deselectFood() {
        mFood = null
        mView?.deselectFood()
    }

    override fun addFood() {
        mView?.addFood(mBox)
    }

    override fun getExpiringFoods() {
        mBox = null

//        mApiFoodRepository.getExpiringFoods()
//                .subscribe(object : DisposableSubscriber<List<Food>>() {
//                    override fun onNext(t: List<Food>?) {
//                        mFoods = t
//                        mView?.setFoods("期限が1週間以内", t)
//                    }
//
//                    override fun onComplete() {}
//
//                    override fun onError(e: Throwable?) {
//                        mView?.showToast(e?.message)
//                    }
//                })
    }

    override fun registerPushToken(userId: Int, token: String) {
        mApiUserRepository.registerPushToken(userId, token)
                .subscribe(object : DisposableSubscriber<User>() {
                    override fun onNext(t: User?) {}

                    override fun onComplete() {
                        mView?.savePushToken(token)
                    }

                    override fun onError(e: Throwable?) {}
                })
    }

    override fun showNotices() {
        mFood?.let {
            mView?.showNotices(it)
        }
    }
}