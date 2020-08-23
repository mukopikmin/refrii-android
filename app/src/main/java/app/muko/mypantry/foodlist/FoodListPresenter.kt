package app.muko.mypantry.foodlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
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
import java.io.File
import javax.inject.Inject

class FoodListPresenter
@Inject
constructor(
        private val boxRepository: ApiBoxRepository,
        private val foodRepository: ApiFoodRepository,
        private val userRepository: ApiUserRepository
) : FoodListContract.Presenter {

    private var view: FoodListContract.View? = null
    var selectedBox: Box? = null
    private var mFoods: List<Food>? = null
    private var mFood: Food? = null

    lateinit var mBoxesLiveData: LiveData<List<Box>>
    lateinit var mFoodsLiveData: LiveData<List<Food>>

    override fun init(view: FoodListContract.View) {
        this.view = view as FoodListActivity
        mBoxesLiveData = boxRepository.dao.getAllLiveData()
        mFoodsLiveData = foodRepository.dao.getAllLiveData()

        mBoxesLiveData.observe(view, Observer {
            view.setNoBoxesMessage(it)
            view.setBoxes(it)
        })

        mFoodsLiveData.observe(view, Observer { foods ->
            val box = selectedBox ?: return@Observer
            val foodsInBox = foods.filter { it.box.id == box.id }

            view.setEmptyMessage(foodsInBox)
            view.setFoods(box.name, foodsInBox)
        })
    }

    fun setBox(box: Box?, foods: List<Food>? = null) {
        box ?: return

        selectedBox = box
        mFoods = mFoodsLiveData.value?.filter { it.box.id == box.id }

        view?.setFoods(box.name, foods)
    }

    override fun getBoxInfo() {
        selectedBox?.let {
            view?.showBoxInfo(it)
        }
    }

    override fun pickBox(menuItemId: Int): Boolean {
        val box = mBoxesLiveData.value?.firstOrNull { menuItemId == it.id } ?: return false

        selectBox(box)

        return true
    }

    override fun getBoxes() {
        // Temporally clear boxes set in drawer, for updating cache
        view?.clearBoxes()

        boxRepository.getAll()
                .doOnSubscribe { view?.showProgressBar() }
                .doFinally { view?.hideProgressBar() }
                .subscribe(object : DisposableSubscriber<List<Box>>() {
                    override fun onNext(t: List<Box>) {}
                    override fun onComplete() {}
                    override fun onError(e: Throwable) {
                        if (e is HttpException) {
                            view?.showToast(e.response()?.message())
                        } else {
                            view?.showToast(e.message)
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

    private fun updateFood(food: Food, imageFile: File? = null) {
        foodRepository.update(food, imageFile)
                .doOnSubscribe { view?.showProgressBar() }
                .doFinally { view?.hideProgressBar() }
                .subscribe(object : CompletableObserver {
                    override fun onComplete() {
                        view?.showSnackbar("${food.name} の数量が更新されました")
                    }

                    override fun onSubscribe(d: Disposable) {}

                    override fun onError(e: Throwable) {
                        view?.showToast(e.message)
                    }
                })
    }

    override fun confirmRemovingFood() {
        view?.showConfirmDialog(mFood)
    }

    override fun removeFood() {
        mFood?.let {
            foodRepository.remove(it)
                    .doOnSubscribe { view?.showProgressBar() }
                    .doFinally { view?.hideProgressBar() }
                    .subscribe(object : CompletableObserver {
                        override fun onComplete() {
                            mFood = null

                            view?.onFoodUpdated(it)
                            view?.showSnackbar("削除が完了しました")
                            getBoxes()
                        }

                        override fun onSubscribe(d: Disposable) {}

                        override fun onError(e: Throwable) {
                            view?.showToast(e.message)
                        }
                    })
        }
    }

    override fun createBox(name: String, notice: String) {
        val box = Box.temp(name, notice)

        boxRepository.create(box)
                .doOnSubscribe { view?.showProgressBar() }
                .doFinally { view?.hideProgressBar() }
                .subscribe(object : CompletableObserver {
                    override fun onComplete() {
                        view?.onBoxCreated()
                        view?.showSnackbar("${box.name} が作成されました")
                        getBoxes()
                    }

                    override fun onSubscribe(d: Disposable) {}

                    override fun onError(e: Throwable) {
                        view?.showToast(e.message)
                    }
                })
    }

    override fun showFood() {
        mFood?.let {
            view?.showFood(it.id, selectedBox)
        }
    }

    override fun selectBox(box: Box) {
        selectedBox = box
        setBox(box)

        foodRepository.getByBox(box.id)
                .doOnSubscribe { view?.showProgressBar() }
                .doFinally { view?.hideProgressBar() }
                .subscribe(object : DisposableSubscriber<List<Food>>() {
                    override fun onNext(t: List<Food>?) {
                        selectedBox?.let {
                            if (it.id == box.id) {
                                setBox(box, t)

                                view?.setEmptyMessage(t)
                            }
                        }
                    }

                    override fun onComplete() {}

                    override fun onError(e: Throwable?) {
                        view?.showToast(e?.message)
                    }
                })
    }


    override fun selectFood(food: Food) {
        mFood = food
        view?.showBottomNavigation(food)
    }

    override fun isFoodSelected(food: Food): Boolean {
        mFood?.let {
            return it.id == food.id
        }

        return false
    }

    override fun deselectFood() {
        mFood = null
        view?.deselectFood()
    }

    override fun addFood() {
        view?.addFood(selectedBox)
    }

    override fun getExpiringFoods() {
        selectedBox = null

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
        userRepository.registerPushToken(userId, token)
                .subscribe(object : DisposableSubscriber<User>() {
                    override fun onNext(t: User?) {}

                    override fun onComplete() {
                        view?.savePushToken(token)
                    }

                    override fun onError(e: Throwable?) {}
                })
    }

    override fun showNotices() {
        mFood?.let {
            view?.showNotices(it)
        }
    }
}