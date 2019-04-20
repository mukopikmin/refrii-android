package com.refrii.client.unitlist

import com.refrii.client.data.models.Unit
import com.refrii.client.data.source.ApiRepository
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
        mApiRepository.getUnits(userId)
                .doOnSubscribe { mView?.showProgressBar() }
                .doOnUnsubscribe { mView?.hideProgressBar() }
                .subscribe({
                    mUnits = it
                    mView?.setUnits(it)
                }, {
                    mView?.showToast(it.message)
                })
    }

    override fun removeUnit(id: Int, userId: Int) {
        mApiRepository.removeUnit(id)
                .doOnSubscribe { mView?.showProgressBar() }
                .doOnUnsubscribe { mView?.hideProgressBar() }
                .subscribe({
                    mView?.showSnackbar("単位を削除しました")
                    getUnits(userId)
                }, {
                    mView?.showToast(it.message)
                })
    }

    override fun getUnit(id: Int) {
        mApiRepository.getUnit(id)
                .subscribe({
                    mView?.onUnitCreateCompleted(it)
                }, {
                    mView?.showToast(it.message)
                })
    }
}