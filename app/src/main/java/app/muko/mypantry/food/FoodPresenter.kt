package app.muko.mypantry.food

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import app.muko.mypantry.data.models.Food
import app.muko.mypantry.data.models.ShopPlan
import app.muko.mypantry.data.models.Unit
import app.muko.mypantry.data.source.ApiFoodRepository
import app.muko.mypantry.data.source.ApiShopPlanRepository
import app.muko.mypantry.data.source.ApiUnitRepository
import io.reactivex.CompletableObserver
import io.reactivex.disposables.Disposable
import io.reactivex.subscribers.DisposableSubscriber
import java.io.File
import javax.inject.Inject

class FoodPresenter
@Inject
constructor(
        private val apiFoodRepository: ApiFoodRepository,
        private val apiShopPlanRepository: ApiShopPlanRepository,
        private val apiUnitRepository: ApiUnitRepository
) : FoodContract.Presenter {

    private var view: FoodContract.View? = null
    lateinit var foodLiveData: LiveData<Food>
    lateinit var unitsLiveData: LiveData<List<Unit>>
    lateinit var shopPlansLiveData: LiveData<List<ShopPlan>>

    override fun init(view: FoodContract.View, foodId: Int) {
        this.view = view as FoodActivity
        foodLiveData = apiFoodRepository.dao.getLiveData(foodId)
        unitsLiveData = apiUnitRepository.dao.getAllLiveData()
        shopPlansLiveData = apiShopPlanRepository.dao.getLiveDataByFood(foodId)

        foodLiveData.observe(view, Observer {
            val shopPlans = shopPlansLiveData.value ?: return@Observer

            view.setFood(it)
            view.setShopPlans(it, shopPlans)
        })
        shopPlansLiveData.observe(view, Observer {
            val food = foodLiveData.value ?: return@Observer

            view.setShopPlans(food, it)
        })
        unitsLiveData.observe(view, Observer {
            view.setUnits(it)
        })
    }

    override fun terminate() {
        foodLiveData.removeObservers(view as FoodActivity)
        unitsLiveData.removeObservers(view as FoodActivity)
        shopPlansLiveData.removeObservers(view as FoodActivity)
    }

    override fun getFood(id: Int) {
        apiFoodRepository.get(id)
                .subscribe(object : DisposableSubscriber<Food>() {
                    override fun onNext(t: Food?) {}
                    override fun onComplete() {}
                    override fun onError(e: Throwable?) {
                        view?.showToast(e?.message)
                    }
                })
    }

    override fun getUnits(boxId: Int) {
        apiUnitRepository.getAll()
                .subscribe(object : DisposableSubscriber<List<Unit>>() {
                    override fun onNext(t: List<Unit>?) {}
                    override fun onComplete() {}
                    override fun onError(e: Throwable?) {
                        view?.showToast(e?.message)
                    }
                })
    }

    override fun updateFood(food: Food, imageFile: File?) {
        apiFoodRepository.update(food, imageFile)
                .doOnSubscribe { view?.showProgressBar() }
                .doFinally { view?.hideProgressBar() }
                .subscribe(object : CompletableObserver {
                    override fun onComplete() {}
                    override fun onSubscribe(d: Disposable) {}
                    override fun onError(e: Throwable) {
                        view?.showToast(e.message)
                    }
                })
    }

    override fun getShopPlans(id: Int) {
        apiShopPlanRepository.getByFood(id)
                .subscribe(object : DisposableSubscriber<List<ShopPlan>>() {
                    override fun onNext(t: List<ShopPlan>?) {}
                    override fun onComplete() {}
                    override fun onError(e: Throwable?) {
                        view?.showToast(e?.message)
                    }
                })
    }

    override fun createShopPlan(shopPlan: ShopPlan) {
        apiShopPlanRepository.create(shopPlan)
                .doOnSubscribe { view?.showProgressBar() }
                .doFinally { view?.hideProgressBar() }
                .subscribe(object : CompletableObserver {
                    override fun onComplete() {}
                    override fun onSubscribe(d: Disposable) {}
                    override fun onError(e: Throwable) {
                        view?.showToast(e.message)
                    }
                })
    }

    override fun completeShopPlan(shopPlan: ShopPlan) {
        shopPlan.done = true

        apiShopPlanRepository.update(shopPlan)
                .doOnSubscribe { view?.showProgressBar() }
                .doFinally { view?.hideProgressBar() }
                .subscribe(object : CompletableObserver {
                    override fun onComplete() {}
                    override fun onSubscribe(d: Disposable) {}
                    override fun onError(e: Throwable) {
                        view?.showToast(e.message)
                    }
                })
    }
}