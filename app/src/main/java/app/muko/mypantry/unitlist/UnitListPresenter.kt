package app.muko.mypantry.unitlist

import app.muko.mypantry.data.models.Unit
import app.muko.mypantry.data.source.ApiUnitRepository
import rx.Subscriber
import javax.inject.Inject

class UnitListPresenter
@Inject
constructor(private val mApiUnitRepository: ApiUnitRepository) : UnitListContract.Presenter {

    private var mView: UnitListContract.View? = null
    private var mUnits: List<Unit>? = null

    override fun takeView(view: UnitListContract.View) {
        mView = view
    }

    override fun getUnits(userId: Int) {
        mApiUnitRepository.getUnits(userId)
                .doOnSubscribe { mView?.showProgressBar() }
                .doOnUnsubscribe { mView?.hideProgressBar() }
                .subscribe(object : Subscriber<List<Unit>>() {
                    override fun onNext(t: List<Unit>?) {
                        mUnits = t
                        mView?.setUnits(t)

                        if (t.isNullOrEmpty()) {
                            mView?.showEmptyMessage()
                        } else {
                            mView?.hideEmptyMessage()
                        }
                    }

                    override fun onCompleted() {}

                    override fun onError(e: Throwable?) {
                        mView?.showToast(e?.message)
                    }
                })
    }

    override fun removeUnit(id: Int, userId: Int) {
        mApiUnitRepository.removeUnit(id)
                .doOnSubscribe { mView?.showProgressBar() }
                .doOnUnsubscribe { mView?.hideProgressBar() }
                .subscribe(object : Subscriber<Void>() {
                    override fun onNext(t: Void?) {}

                    override fun onCompleted() {
                        mView?.showSnackbar("単位を削除しました")
                        getUnits(userId)
                    }

                    override fun onError(e: Throwable?) {
                        mView?.showToast(e?.message)
                    }
                })
    }

    override fun getUnit(id: Int) {
        mApiUnitRepository.getUnit(id)
                .subscribe(object : Subscriber<Unit>() {
                    override fun onNext(t: Unit?) {
                        mView?.onUnitCreateCompleted(t)
                    }

                    override fun onCompleted() {}

                    override fun onError(e: Throwable?) {
                        mView?.showToast(e?.message)
                    }
                })
    }
}