package app.muko.mypantry.unit

import app.muko.mypantry.data.models.Unit
import app.muko.mypantry.data.source.ApiUnitRepository
import io.reactivex.CompletableObserver
import io.reactivex.disposables.Disposable
import io.reactivex.subscribers.DisposableSubscriber
import javax.inject.Inject

class UnitPresenter
@Inject
constructor(
        private val mApiUnitRepository: ApiUnitRepository
) : UnitContract.Presenter {

    private var mView: UnitContract.View? = null
    private var mUnit: Unit? = null

    override fun takeView(view: UnitContract.View) {
        mView = view
    }

    override fun getUnit(id: Int) {
        mApiUnitRepository.get(id)
                .doOnSubscribe { mView?.onLoading() }
                .doFinally { mView?.onLoaded() }
                .subscribe(object : DisposableSubscriber<Unit>() {
                    override fun onNext(t: Unit?) {
                        mUnit = t
                        mView?.setUnit(t)
                    }

                    override fun onComplete() {}

                    override fun onError(e: Throwable?) {
                        mView?.showToast(e?.message)
                    }
                })
    }

    override fun updateUnit(unit: Unit) {
        mUnit?.let {
            mApiUnitRepository.update(unit)
                    .doOnSubscribe { mView?.onLoading() }
                    .doFinally { mView?.onLoaded() }
                    .subscribe(object : CompletableObserver {
                        override fun onComplete() {
                            mView?.showSnackbar("${unit.label} を更新しました")
                        }

                        override fun onSubscribe(d: Disposable) {}

                        override fun onError(e: Throwable) {
                            mView?.showToast(e?.message)
                        }
                    })
        }
    }
}