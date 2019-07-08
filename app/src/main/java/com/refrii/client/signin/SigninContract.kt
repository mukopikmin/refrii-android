package com.refrii.client.signin

import com.refrii.client.data.models.User

interface SigninContract {

    interface View {
        fun onLoading()
        fun onLoaded()
        fun onLoginCompleted()
        fun saveAccount(user: User?)
        fun showToast(message: String?)
    }

    interface Presenter {
        fun takeView(view: View)
        fun signup()
        fun verifyAccount()
    }
}