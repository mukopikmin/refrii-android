package app.muko.mypantry.food

import androidx.lifecycle.LiveData
import app.muko.mypantry.data.dao.LocalDatabase
import app.muko.mypantry.data.models.Food
import app.muko.mypantry.data.models.ShopPlan
import app.muko.mypantry.data.models.Unit
import app.muko.mypantry.data.source.ApiBoxRepository
import app.muko.mypantry.data.source.ApiFoodRepository
import app.muko.mypantry.data.source.ApiShopPlanRepository
import app.muko.mypantry.data.source.ApiUnitRepository
import io.reactivex.CompletableObserver
import io.reactivex.disposables.Disposable
import io.reactivex.subscribers.DisposableSubscriber
import javax.inject.Inject

class FoodPresenter
@Inject
constructor(
        private val localDatabase: LocalDatabase,
        private val apiFoodRepository: ApiFoodRepository,
        private val apiBoxRepository: ApiBoxRepository,
        private val apiShopPlanRepository: ApiShopPlanRepository,
        private val apiUnitRepository: ApiUnitRepository
) : FoodContract.Presenter {

    private var view: FoodContract.View? = null
    lateinit var foodLiveData: LiveData<Food>
    lateinit var unitsLiveData: LiveData<List<Unit>>
    lateinit var shopPlansLiveData: LiveData<List<ShopPlan>>

    override fun takeView(view: FoodContract.View) {
        this.view = view
    }

    override fun initLiveData(foodId: Int) {
        foodLiveData = localDatabase.foodDao().getLiveData(foodId)
        unitsLiveData = apiUnitRepository.dao.getAllLiveData()//localDatabase.unitDao().getAllLiveData()
        shopPlansLiveData = localDatabase.shopPlanDao().getLiveDataByFood(foodId)
    }

//    override fun getFoodLiveData(id: Int): LiveData<Food> {
//        return mLocalDatabase.foodDao().getLiveData(id)
//    }
//
//    override fun getUnitsLiveData(): LiveData<List<Unit>> {
//        return mLocalDatabase.unitDao().getAllLiveData()
//    }
//
//    override fun getShopPlansLiveData(foodId: Int): LiveData<List<ShopPlan>> {
//        return mLocalDatabase.shopPlanDao().getLiveDataByFood(foodId)
//    }

    override fun getFood(id: Int) {
        apiFoodRepository.get(id)
                .subscribe(object : DisposableSubscriber<Food>() {
                    override fun onNext(t: Food?) {
//                        mView?.setFood(t)
                    }

                    override fun onComplete() {}

                    override fun onError(e: Throwable?) {
                        view?.showToast(e?.message)
                    }
                })
    }

    override fun getUnits(boxId: Int) {
        apiUnitRepository.getAll()
                .subscribe(object : DisposableSubscriber<List<Unit>>() {
                    override fun onNext(t: List<Unit>?) {
//                        mView?.setUnits(t)
                    }

                    override fun onComplete() {}

                    override fun onError(e: Throwable?) {
                        view?.showToast(e?.message)
                    }
                })
    }

    override fun updateFood(food: Food) {
        apiFoodRepository.update(food)
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
                    override fun onNext(t: List<ShopPlan>?) {
//                        mView?.setShopPlans(t?.filter { !it.done })
                    }

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