package com.refrii.client.newunit

import com.refrii.client.data.api.models.Unit
import com.refrii.client.data.api.source.ApiRepository
import com.refrii.client.data.api.source.ApiRepositoryCallback
import javax.inject.Inject

class NewUnitPresenter
@Inject
constructor(private val mApiRepository: ApiRepository) : NewUnitContract.Presenter {

    private var mView: NewUnitContract.View? = null
    private var mUnit: Unit? = null

    override fun takeView(view: NewUnitContract.View) {
        mView = view
    }

    override fun createUnit(label: String, amount: Double) {
        mView?.showProgressBar()

        mApiRepository.createUnit(label, amount, object : ApiRepositoryCallback<Unit> {
            override fun onNext(t: Unit?) {
                mUnit = t
            }

            override fun onCompleted() {
                mView?.hideProgressBar()
                mView?.onCreateCompleted(mUnit)
            }

            override fun onError(e: Throwable?) {
                e?.message?.let {
                    mView?.showToast(it)
                }
            }
        })
    }
}