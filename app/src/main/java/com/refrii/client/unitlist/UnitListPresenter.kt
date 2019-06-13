package com.refrii.client.unitlist

import com.refrii.client.data.models.Unit
import com.refrii.client.data.source.ApiUnitRepository
import javax.inject.Inject

class UnitListPresenter
@Inject
constructor(private val mApiUnitRepository: ApiUnitRepository) : UnitListContract.Presenter {

    private var mView: UnitListContract.View? = null
    private var mUnits: List<Unit>? = null

    override fun takeView(view: UnitListContract.View) {
        mView = view
    }

    override fun getUnits(userId: Int) {
        mApiUnitRepository.getUnits(userId)
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
        mApiUnitRepository.removeUnit(id)
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
        mApiUnitRepository.getUnit(id)
                .subscribe({
                    mView?.onUnitCreateCompleted(it)
                }, {
                    mView?.showToast(it.message)
                })
    }
}