package com.refrii.client.unit

import com.refrii.client.data.models.Unit

interface UnitContract {

    interface View {
        fun setUnit(unit: Unit?)
        fun onBeforeEdit()
        fun onEdited()
        fun showEditLabelDialog(label: String?)
        fun showEditStepDIalog(step: Double?)
        fun onLoading()
        fun onLoaded()
        fun showToast(message: String?)
        fun showSnackbar(message: String?)
    }

    interface Presenter {
        fun takeView(view: View)
        fun getUnit(id: Int)
        fun updateUnit()
        fun editLabel()
        fun editStep()
        fun updateLabel(label: String)
        fun updateStep(step: Double)
    }
}