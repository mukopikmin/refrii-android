package com.refrii.client.unitlist

import com.refrii.client.data.api.models.Unit
import com.refrii.client.data.api.source.ApiRepository
import com.refrii.client.data.api.source.ApiRepositoryCallback
import javax.inject.Inject

class UnitListPresenter
@Inject
constructor(private val mApiRepository: ApiRepository) : UnitListContract.Presenter {

    private var mView: UnitListContract.View? = null
    private var mUnits: List<Unit>? = null

    override fun takeView(view: UnitListContract.View) {
        mView = view
    }

    override fun getUnits(userId: Int) {
        mView?.showProgressBar()

        mUnits = mApiRepository.getUnits(userId, object : ApiRepositoryCallback<List<Unit>> {
            override fun onNext(t: List<Unit>?) {
                mUnits = t
                mView?.setUnits(t)
            }

            override fun onCompleted() {
                mView?.hideProgressBar()
            }

            override fun onError(e: Throwable?) {
                mView?.showToast(e?.message)
            }
        })

        mView?.setUnits(mUnits)
    }

    override fun removeUnit(id: Int, userId: Int) {
        mView?.showProgressBar()

        mApiRepository.removeUnit(id, object : ApiRepositoryCallback<Void> {
            override fun onNext(t: Void?) {}

            override fun onCompleted() {
                mView?.onUnitUpdateCompleted()
                mView?.hideProgressBar()
                mView?.showSnackbar("単位を削除しました")

                getUnits(userId)
            }

            override fun onError(e: Throwable?) {
                mView?.showToast(e?.message)
            }
        })
    }

    override fun getUnit(id: Int): Unit? {
        return mApiRepository.getUnit(id, object : ApiRepositoryCallback<Unit> {
            override fun onNext(t: Unit?) {}
            override fun onCompleted() {}
            override fun onError(e: Throwable?) {}
        })
    }
}