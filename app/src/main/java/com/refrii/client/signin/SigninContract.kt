package com.refrii.client.signin

interface SigninContract {

    interface View {
        fun onLoading()
        fun onLoaded()
        fun showToast(message: String?)
    }

    interface Presenter {
        fun takeView(view: View)
    }
}