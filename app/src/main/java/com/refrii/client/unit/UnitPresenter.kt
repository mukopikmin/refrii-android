package com.refrii.client.unit

import com.refrii.client.data.api.models.Unit
import com.refrii.client.data.api.source.ApiRepository
import com.refrii.client.data.api.source.ApiRepositoryCallback
import javax.inject.Inject

class UnitPresenter
@Inject
constructor(private val mApiRepository: ApiRepository) : UnitContract.Presenter {

    private var mView: UnitContract.View? = null
    private var mUnit: Unit? = null

    override fun takeView(view: UnitContract.View) {
        mView = view
    }

    override fun getUnit(id: Int) {
        mView?.onLoading()

        mApiRepository.getUnit(id, object : ApiRepositoryCallback<Unit> {
            override fun onNext(t: Unit?) {
                mUnit = t
                mView?.setUnit(t)
            }

            override fun onCompleted() {
                mView?.onBeforeEdit()
                mView?.onLoaded()
            }

            override fun onError(e: Throwable?) {
                mView?.showToast(e?.message)
            }
        })
    }

    override fun updateUnit() {
        mView?.onLoading()

        mUnit?.let {
            mApiRepository.updateUnit(it, object : ApiRepositoryCallback<Unit> {
                override fun onNext(t: Unit?) {
                    mUnit = t
                    mView?.setUnit(t)
                    mView?.showSnackbar("Unit ${t?.label} is updated successfully")
                }

                override fun onCompleted() {
                    mView?.onBeforeEdit()
                    mView?.onLoaded()
                }

                override fun onError(e: Throwable?) {
                    mView?.showToast(e?.message)
                }
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