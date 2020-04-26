package app.muko.mypantry.newfood

import app.muko.mypantry.data.models.Box
import app.muko.mypantry.data.models.Food
import app.muko.mypantry.data.models.Unit
import app.muko.mypantry.data.source.ApiBoxRepository
import app.muko.mypantry.data.source.ApiFoodRepository
import rx.Subscriber
import java.util.*
import javax.inject.Inject

class NewFoodPresenter
@Inject
constructor(
        private val mApiBoxRepository: ApiBoxRepository,
        private val mApiFoodRepository: ApiFoodRepository) : NewFoodContract.Presenter {

    private var mView: NewFoodContract.View? = null
    private var mUnits: List<Unit>? = null
    private var mBox: Box? = null
    private var mFood: Food? = null

    override fun takeView(view: NewFoodContract.View) {
        mView = view
    }

    override fun createFood(name: String, amount: Double, unit: Unit?, expirationDate: Date) {
        unit ?: return

        mBox?.let { box ->
            mApiFoodRepository.createFood(name, amount, box, unit, expirationDate)
                    .doOnSubscribe { mView?.showProgressBar() }
                    .doOnUnsubscribe { mView?.hideProgressBar() }
                    .subscribe(object : Subscriber<Food>() {
                        override fun onNext(t: Food?) {
                            mFood = t
                            mView?.createCompleted(t)
                        }

                        override fun onCompleted() {}

                        override fun onError(e: Throwable?) {
                            mView?.showToast(e?.message)
                        }
                    })
        }
    }

    override fun getUnits(boxId: Int) {
        mApiBoxRepository.getUnitsForBox(boxId)
                .subscribe(object : Subscriber<List<Unit>>() {
                    override fun onNext(t: List<Unit>?) {
                        mUnits = t
                        mView?.setUnits(t)

                        if (t.isNullOrEmpty()) {
                            mView?.goToAddUnit()
                        }
                    }

                    override fun onCompleted() {}

                    override fun onError(e: Throwable?) {
                        mView?.showToast(e?.message)
                    }
                })
    }

    override fun getBox(id: Int) {
        mApiBoxRepository.getBox(id)
                .subscribe(object: Subscriber<Box>() {
                    override fun onNext(t: Box?) {
                        mBox = t
                        mView?.setBox(t)
                    }

                    override fun onCompleted() { }

                    override fun onError(e: Throwable?) {
                        mView?.showToast(e?.message)
                    }
                })
    }

    override fun pickUnit(label: String): Unit? {
        return mUnits?.firstOrNull { it.label == label }
    }
}