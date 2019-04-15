package com.refrii.client.signin

import com.refrii.client.data.api.source.ApiRepository
import javax.inject.Inject

class SigninPresenter
@Inject
constructor(private val mApiRepository: ApiRepository) : SigninContract.Presenter {

    private var mView: SigninContract.View? = null

    override fun takeView(view: SigninContract.View) {
        mView = view
    }

    override fun verifyAccount() {
        mApiRepository.verify()
                .subscribe({
                    mView?.saveAccount(it)
                    mView?.onLoginCompleted()
                }, {
                    mView?.showToast(it.message)
                })
    }
}