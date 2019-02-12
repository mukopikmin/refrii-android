package com.refrii.client.signin

import com.refrii.client.data.api.models.Credential

interface SigninContract {

    interface View {
        fun onAuthCompleted(credential: Credential?)
        fun onLoading()
        fun onLoaded()
        fun showToast(message: String?)
    }

    interface Presenter {
        fun takeView(view: View)
    }
}