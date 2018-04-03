package com.refrii.client.newunit

interface NewUnitContract {

    interface View {
        fun showProgressBar()
        fun hideProgressBar()
        fun showToast(message: String)
    }

    interface Presenter {
        fun takeView(view: NewUnitContract.View)
        fun createUnit(label: String, amount: Double)
    }
}