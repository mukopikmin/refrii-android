package com.refrii.client.signin

import com.refrii.client.data.models.User
import com.refrii.client.data.source.ApiUserRepository
import rx.Subscriber
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
                .subscribe(object : Subscriber<User>() {
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

    override fun signup() {
        mApiUserRepository.signup()
                .subscribe(object : Subscriber<User>() {
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