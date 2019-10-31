package com.refrii.client.unit

import com.refrii.client.data.models.Unit

interface UnitContract {

    interface View {
        fun setUnit(unit: Unit?)
        fun onLoading()
        fun onLoaded()
        fun showToast(message: String?)
        fun showSnackbar(message: String?)
    }

    interface Presenter {
        fun takeView(view: View)
        fun getUnit(id: Int)
        fun updateUnit(label: String?, step: Double)
    }
}