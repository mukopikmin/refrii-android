package app.muko.mypantry.newunit

import app.muko.mypantry.data.models.Unit

interface NewUnitContract {

    interface View {
        fun showProgressBar()
        fun hideProgressBar()
        fun showToast(message: String?)
        fun onCreateCompleted(unit: Unit?)
    }

    interface Presenter {
        fun takeView(view: NewUnitContract.View)
        fun createUnit(label: String, amount: Double)
    }
}