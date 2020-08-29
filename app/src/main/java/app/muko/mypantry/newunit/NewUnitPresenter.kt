package app.muko.mypantry.newunit

import app.muko.mypantry.data.models.Unit
import app.muko.mypantry.data.source.ApiUnitRepository
import io.reactivex.CompletableObserver
import io.reactivex.disposables.Disposable
import javax.inject.Inject

class NewUnitPresenter
@Inject
constructor(
        private val apiUnitRepository: ApiUnitRepository
) : NewUnitContract.Presenter {

    private var view: NewUnitContract.View? = null

    override fun init(view: NewUnitContract.View) {
        this.view = view
    }

    override fun createUnit(unit: Unit) {
        apiUnitRepository.create(unit)
                .doOnSubscribe { view?.showProgressBar() }
                .doFinally { view?.hideProgressBar() }
                .subscribe(object : CompletableObserver {
                    override fun onComplete() {
                        view?.onCreateCompleted(unit)
                    }

                    override fun onSubscribe(d: Disposable) {}

                    override fun onError(e: Throwable) {
                        view?.showToast(e.message)
                    }
                })
    }
}