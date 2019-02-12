package com.refrii.client.signin

import javax.inject.Inject

class SigninPresenter
@Inject
constructor() : SigninContract.Presenter {

    private var mView: SigninContract.View? = null

    override fun takeView(view: SigninContract.View) {
        mView = view
    }
}