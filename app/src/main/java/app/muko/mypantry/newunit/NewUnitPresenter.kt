package app.muko.mypantry.newunit

import app.muko.mypantry.data.models.Unit
import app.muko.mypantry.data.source.ApiUnitRepository
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
                .doOnUnsubscribe { mView?.hideProgressBar() }
                .subscribe({
                    mUnit = it
                    mView?.onCreateCompleted(mUnit)
                }, {
                    mView?.showToast(it.message)
                })
    }
}