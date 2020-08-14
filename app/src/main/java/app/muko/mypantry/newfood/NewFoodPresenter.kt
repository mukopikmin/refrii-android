package app.muko.mypantry.newfood

import app.muko.mypantry.data.models.Box
import app.muko.mypantry.data.models.Food
import app.muko.mypantry.data.models.Unit
import app.muko.mypantry.data.source.ApiBoxRepository
import app.muko.mypantry.data.source.ApiFoodRepository
import javax.inject.Inject

class NewFoodPresenter
@Inject
constructor(
        private val mApiBoxRepository: ApiBoxRepository,
        private val mApiFoodRepository: ApiFoodRepository) : NewFoodContract.Presenter {

    private var mView: NewFoodContract.View? = null
    private var mUnits: List<Unit>? = null
    var box: Box? = null
    private var mFood: Food? = null

    override fun takeView(view: NewFoodContract.View) {
        mView = view
    }

    override fun createFood(food: Food) {
//        TODO: Rewrite
//        mApiFoodRepository.create(food)
//                .doOnSubscribe { mView?.showProgressBar() }
//                .doFinally { mView?.hideProgressBar() }
//                .subscribe(object : DisposableSubscriber<Food>() {
//                    override fun onNext(t: Food?) {
//                        mFood = t
//                        mView?.createCompleted(t)
//                    }
//
//                    override fun onComplete() {}
//
//                    override fun onError(e: Throwable?) {
//                        mView?.showToast(e?.message)
//                    }
//                })
    }

    override fun getUnits(boxId: Int) {
//        TODO: Rewrite
//        mApiBoxRepository.getUnitsForBox(boxId)
//                .subscribe(object : DisposableSubscriber<List<Unit>>() {
//                    override fun onNext(t: List<Unit>?) {
//                        mUnits = t
//                        mView?.setUnits(t)
//
//                        if (t.isNullOrEmpty()) {
//                            mView?.goToAddUnit()
//                        }
//                    }
//
//                    override fun onComplete() {}
//
//                    override fun onError(e: Throwable?) {
//                        mView?.showToast(e?.message)
//                    }
//                })
    }

    override fun getBox(id: Int) {
//        TODO: Rewrite
//        mApiBoxRepository.getBox(id)
//                .subscribe(object : DisposableSubscriber<Box>() {
//                    override fun onNext(t: Box?) {
//                        box = t
//                        mView?.setBox(t)
//                    }
//
//                    override fun onComplete() {}
//
//                    override fun onError(e: Throwable?) {
//                        mView?.showToast(e?.message)
//                    }
//                })
    }

    override fun pickUnit(label: String): Unit? {
        return mUnits?.firstOrNull { it.label == label }
    }
}