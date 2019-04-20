package com.refrii.client.newunit

import com.refrii.client.data.models.Unit
import com.refrii.client.data.source.ApiRepository
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
        mApiRepository.createUnit(label, amount)
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