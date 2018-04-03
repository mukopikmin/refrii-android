package com.refrii.client.newunit

import com.refrii.client.data.api.models.Unit
import com.refrii.client.data.api.source.ApiRepository
import com.refrii.client.data.api.source.ApiRepositoryCallback
import javax.inject.Inject

class NewUnitPresenter
@Inject
constructor(private val mApiRepository: ApiRepository) : NewUnitContract.Presenter {

    private var mView: NewUnitContract.View? = null

    override fun takeView(view: NewUnitContract.View) {
        mView = view
    }

    override fun createUnit(label: String, amount: Double) {
        mView?.showProgressBar()

        mApiRepository.createUnit(label, amount, object : ApiRepositoryCallback<Unit> {
            override fun onNext(t: Unit?) {}

            override fun onCompleted() {
                mView?.hideProgressBar()
            }

            override fun onError(e: Throwable?) {
                e?.message?.let {
                    mView?.showToast(it)
                }
            }

        })
    }
}