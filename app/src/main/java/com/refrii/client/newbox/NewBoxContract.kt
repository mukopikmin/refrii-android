package com.refrii.client.newbox

interface NewBoxContract {

    interface View {
        fun onLoading()
        fun onLoaded()
        fun showToast(message: String?)
        fun onCreateSuccess()
    }

    interface Presenter {
        fun takeView(view: NewBoxContract.View)
        fun createBox(name: String, notice: String)
    }
}