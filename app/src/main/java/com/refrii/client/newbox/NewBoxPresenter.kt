package com.refrii.client.newbox

import com.refrii.client.data.api.models.Box
import com.refrii.client.data.api.source.ApiRepository
import com.refrii.client.data.api.source.ApiRepositoryCallback
import javax.inject.Inject

class NewBoxPresenter
@Inject
constructor(private val mApiRepository: ApiRepository) : NewBoxContract.Presenter {

    private var mView: NewBoxContract.View? = null
    private var mBox: Box? = null

    override fun takeView(view: NewBoxContract.View) {
        mView = view
    }

    override fun createBox(name: String, notice: String) {
        mView?.onLoading()

        mApiRepository.createBox(object : ApiRepositoryCallback<Box> {
            override fun onNext(t: Box?) {
                mBox = t
            }

            override fun onCompleted() {
                mView?.onLoaded()
                mView?.onCreateSuccess()
            }

            override fun onError(e: Throwable?) {
                mView?.onLoaded()
                mView?.showToast(e?.message)
            }
        }, name, notice)
    }
}