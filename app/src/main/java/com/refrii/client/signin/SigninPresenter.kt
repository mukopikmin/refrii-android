package com.refrii.client.signin

import com.refrii.client.data.api.models.Credential
import com.refrii.client.data.api.source.ApiRepository
import com.refrii.client.data.api.source.ApiRepositoryCallback
import com.refrii.client.data.google.GoogleRepository
import com.refrii.client.data.google.GoogleRepositoryCallback
import javax.inject.Inject

class SigninPresenter
@Inject
constructor(
        private val mGoogleRepository: GoogleRepository,
        private val mApiRepository: ApiRepository
) : SigninContract.Presenter {

    private var mView: SigninContract.View? = null
    private var mCredential: Credential? = null

    override fun takeView(view: SigninContract.View) {
        mView = view
    }

    override fun auth(accountName: String?) {
        accountName ?: return

        mGoogleRepository.getToken(accountName, object : GoogleRepositoryCallback {
            override fun onSuccess(token: String) {
                getJwt(token)
            }

            override fun onError() {
                mView?.showToast("Error getting google token")
            }
        })
    }

    private fun getJwt(googleToken: String) {
        mView?.onLoading()

        mApiRepository.auth(googleToken, object : ApiRepositoryCallback<Credential> {
            override fun onNext(t: Credential?) {
                mCredential = t
                mView?.onAuthCompleted(t)
            }

            override fun onCompleted() {}

            override fun onError(e: Throwable?) {
                mView?.showToast(e?.message)
                mView?.onLoaded()
            }
        })
    }
}