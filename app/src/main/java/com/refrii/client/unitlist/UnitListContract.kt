package com.refrii.client.unitlist

import com.refrii.client.data.api.models.Unit

interface UnitListContract {

    interface View {
        fun setUnits(units: List<Unit>?)
        fun showProgressBar()
        fun hideProgressBar()
        fun showToast(message: String?)
        fun showSnackbar(message: String?)
    }

    interface Presenter {
        fun takeView(view: UnitListContract.View)
        fun getUnits(userId: Int)
        fun removeUnit(id: Int, userId: Int)
    }
}