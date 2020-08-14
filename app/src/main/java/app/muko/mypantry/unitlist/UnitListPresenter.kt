package app.muko.mypantry.unitlist

import app.muko.mypantry.data.models.Unit
import app.muko.mypantry.data.source.ApiUnitRepository
import io.reactivex.CompletableObserver
import io.reactivex.disposables.Disposable
import io.reactivex.subscribers.DisposableSubscriber
import javax.inject.Inject

class UnitListPresenter
@Inject
constructor(private val mApiUnitRepository: ApiUnitRepository) : UnitListContract.Presenter {

    private var mView: UnitListContract.View? = null
    var mUnits: List<Unit>? = null

    override fun takeView(view: UnitListContract.View) {
        mView = view
    }

    override fun getUnits(userId: Int) {
//        TODO
//        mApiUnitRepository.getUnits(userId)
//                .doOnSubscribe { mView?.showProgressBar() }
//                .doFinally { mView?.hideProgressBar() }
//                .subscribe(object : DisposableSubscriber<List<Unit>>() {
//                    override fun onNext(t: List<Unit>?) {
//                        mUnits = t
//                        mView?.setUnits(t)
//
//                        if (t.isNullOrEmpty()) {
//                            mView?.showEmptyMessage()
//                        } else {
//                            mView?.hideEmptyMessage()
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

    override fun removeUnit(unit: Unit) {
        mApiUnitRepository.remove(unit)
                .doOnSubscribe { mView?.showProgressBar() }
                .doFinally { mView?.hideProgressBar() }
                .subscribe(object : CompletableObserver {
                    override fun onComplete() {
                        mView?.showSnackbar("単位を削除しました")
                    }

                    override fun onSubscribe(d: Disposable) {}

                    override fun onError(e: Throwable) {
                        mView?.showToast(e?.message)
                    }
                })
    }

    override fun getUnit(id: Int) {
        mApiUnitRepository.get(id)
                .subscribe(object : DisposableSubscriber<Unit>() {
                    override fun onNext(t: Unit?) {
                        mView?.onUnitCreateCompleted(t)
                    }

                    override fun onComplete() {}

                    override fun onError(e: Throwable?) {
                        mView?.showToast(e?.message)
                    }
                })
    }
}