package app.muko.mypantry.newunit

import app.muko.mypantry.data.models.Unit
import app.muko.mypantry.data.source.ApiUnitRepository
import io.reactivex.CompletableObserver
import io.reactivex.disposables.Disposable
import javax.inject.Inject

class NewUnitPresenter
@Inject
constructor(
        private val mApiUnitRepository: ApiUnitRepository
) : NewUnitContract.Presenter {

    private var mView: NewUnitContract.View? = null
    private var mUnit: Unit? = null

    override fun takeView(view: NewUnitContract.View) {
        mView = view
    }

    override fun createUnit(unit: Unit) {
        mApiUnitRepository.create(unit)
                .doOnSubscribe { mView?.showProgressBar() }
                .doFinally { mView?.hideProgressBar() }
                .subscribe(object : CompletableObserver {
                    override fun onComplete() {
                        mView?.onCreateCompleted(unit)
                    }

                    override fun onSubscribe(d: Disposable) {}

                    override fun onError(e: Throwable) {
                        mView?.showToast(e.message)
                    }
                })
    }
}