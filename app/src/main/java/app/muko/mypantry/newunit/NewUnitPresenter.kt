package app.muko.mypantry.newunit

import app.muko.mypantry.data.models.Unit
import app.muko.mypantry.data.source.ApiUnitRepository
import io.reactivex.subscribers.DisposableSubscriber
import javax.inject.Inject

class NewUnitPresenter
@Inject
constructor(private val mApiUnitRepository: ApiUnitRepository) : NewUnitContract.Presenter {

    private var mView: NewUnitContract.View? = null
    private var mUnit: Unit? = null

    override fun takeView(view: NewUnitContract.View) {
        mView = view
    }

    override fun createUnit(label: String, amount: Double) {
        mApiUnitRepository.createUnit(label, amount)
                .doOnSubscribe { mView?.showProgressBar() }
                .doFinally { mView?.hideProgressBar() }
                .subscribe(object : DisposableSubscriber<Unit>() {
                    override fun onComplete() {}

                    override fun onNext(t: Unit?) {
                        mUnit = t
                        mView?.onCreateCompleted(mUnit)
                    }

                    override fun onError(t: Throwable?) {
                        mView?.showToast(t?.message)
                    }
                })
    }
}