package com.refrii.client.signin

import com.refrii.client.data.source.ApiUserRepository
import javax.inject.Inject

class SigninPresenter
@Inject
constructor(private val mApiUserRepository: ApiUserRepository) : SigninContract.Presenter {

    private var mView: SigninContract.View? = null

    override fun takeView(view: SigninContract.View) {
        mView = view
    }

    override fun verifyAccount() {
        mApiUserRepository.verify()
                .subscribe({
                    mView?.saveAccount(it)
                    mView?.onLoginCompleted()
                }, {
                    mView?.showToast(it.message)
                })
    }
}