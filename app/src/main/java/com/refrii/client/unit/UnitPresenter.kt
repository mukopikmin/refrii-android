package com.refrii.client.unit

import com.refrii.client.data.models.Unit
import com.refrii.client.data.source.ApiUnitRepository
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
                .subscribe({
                    mUnit = it
                    mView?.setUnit(it)
                    mView?.onBeforeEdit()
                }, {
                    mView?.showToast(it.message)
                })
    }

    override fun updateUnit() {
        mUnit?.let { unit ->
            mApiUnitRepository.updateUnit(unit)
                    .doOnSubscribe { mView?.onLoading() }
                    .doOnUnsubscribe { mView?.onLoaded() }
                    .subscribe({
                        mUnit = it
                        mView?.setUnit(it)
                        mView?.showSnackbar("Unit ${it.label} is updated successfully")

                        mView?.onBeforeEdit()
                    }, {
                        mView?.showToast(it.message)
                    })
        }
    }

    override fun editLabel() {
        mView?.showEditLabelDialog(mUnit?.label)
    }

    override fun editStep() {
        mView?.showEditStepDIalog(mUnit?.step)
    }

    override fun updateLabel(label: String) {
        mUnit?.label = label
        mView?.setUnit(mUnit)
    }

    override fun updateStep(step: Double) {
        mUnit?.step = step
        mView?.setUnit(mUnit)
    }
}