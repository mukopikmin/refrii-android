package com.refrii.client.welcome

interface WelcomeContract {

    interface View {
        fun onLoading()
        fun onLoaded()
        fun onCreateBoxCompleted()
        fun showToast(message: String?)
    }

    interface Presenter {
        fun takeView(view: View)
        fun createBox(name: String)
    }
}