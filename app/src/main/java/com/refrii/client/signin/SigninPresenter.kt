package com.refrii.client.signin

import com.refrii.client.data.api.models.User
import com.refrii.client.data.api.source.ApiRepository
import com.refrii.client.data.api.source.ApiRepositoryCallback
import javax.inject.Inject

class SigninPresenter
@Inject
constructor(private val mApiRepository: ApiRepository) : SigninContract.Presenter {

    private var mView: SigninContract.View? = null

    override fun takeView(view: SigninContract.View) {
        mView = view
    }

    override fun verifyAccount() {
        mApiRepository.verify(object : ApiRepositoryCallback<User> {
            override fun onNext(t: User?) {
                mView?.saveAccount(t)
            }

            override fun onCompleted() {
                mView?.onLoginCompleted()
            }

            override fun onError(e: Throwable?) {
                mView?.showToast(e?.message)
            }
        })
    }
}