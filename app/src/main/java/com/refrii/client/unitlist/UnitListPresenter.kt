package com.refrii.client.unitlist

import com.refrii.client.data.api.models.Unit
import com.refrii.client.data.api.source.ApiRepository
import com.refrii.client.data.api.source.ApiRepositoryCallback
import javax.inject.Inject

class UnitListPresenter
@Inject
constructor(private val mApiRepository: ApiRepository) : UnitListContract.Presenter {

    private var mView: UnitListContract.View? = null
    var mUnits: MutableList<Unit>? = null

    override fun takeView(view: UnitListContract.View) {
        mView = view
    }

    override fun getUnits(userId: Int) {
        mView?.showProgressBar()

        mApiRepository.getUnits(userId, object : ApiRepositoryCallback<List<Unit>> {
            override fun onNext(t: List<Unit>?) {
                t?.let {
                    mUnits = it.toMutableList()
                    mView?.setUnits(it)
                }
            }

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

    override fun removeUnit(id: Int) {
        mView?.showProgressBar()

        mApiRepository.removeUnit(id, object : ApiRepositoryCallback<Void> {
            override fun onNext(t: Void?) {}

            override fun onCompleted() {
                mView?.hideProgressBar()
                mView?.showSnackbar("Unit is removed")
            }

            override fun onError(e: Throwable?) {
                e?.message?.let {
                    mView?.showToast(it)
                }
            }

        })
    }
}