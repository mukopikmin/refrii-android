package app.muko.mypantry.unit

import app.muko.mypantry.data.models.Unit
import app.muko.mypantry.data.source.ApiUnitRepository
import rx.Subscriber
import javax.inject.Inject

class UnitPresenter
@Inject
constructor(private val mApiUnitRepository: ApiUnitRepository) : UnitContract.Presenter {

    private var mView: UnitContract.View? = null
    private var mUnit: Unit? = null

    override fun takeView(view: UnitContract.View) {
        mView = view
    }

    override fun getUnit(id: Int) {
        mApiUnitRepository.getUnit(id)
                .doOnSubscribe { mView?.onLoading() }
                .doOnUnsubscribe { mView?.onLoaded() }
                .subscribe(object : Subscriber<Unit>() {
                    override fun onNext(t: Unit?) {
                        mUnit = t
                        mView?.setUnit(t)
                    }

                    override fun onCompleted() {}

                    override fun onError(e: Throwable?) {
                        mView?.showToast(e?.message)
                    }
                })
    }

    override fun updateUnit(label: String?, step: Double) {
        mUnit?.let {
            mApiUnitRepository.updateUnit(it.id, label, step)
                    .doOnSubscribe { mView?.onLoading() }
                    .doOnUnsubscribe { mView?.onLoaded() }
                    .subscribe(object : Subscriber<Unit>() {
                        override fun onNext(t: Unit?) {
                            mUnit = t
                            mView?.setUnit(t)
                            mView?.showSnackbar("${t?.label} を更新しました")
                        }

                        override fun onCompleted() {}

                        override fun onError(e: Throwable?) {
                            mView?.showToast(e?.message)
                        }
                    })
        }
    }
}