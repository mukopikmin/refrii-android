package com.refrii.client.newfood

import com.refrii.client.data.models.Box
import com.refrii.client.data.models.Food
import com.refrii.client.data.models.Unit
import java.util.*

interface NewFoodContract {

    interface View {
        fun setUnits(units: List<Unit>?)
        fun setBox(box: Box?)
        fun createCompleted(food: Food?)
        fun showProgressBar()
        fun hideProgressBar()
        fun showToast(message: String?)
        fun goToAddUnit()
    }

    interface Presenter {
        fun takeView(view: View)
        fun createFood(name: String, notice: String, amount: Double, unit: Unit?, expirationDate: Date)
        fun getUnits(boxId: Int)
        fun getBox(id: Int)
        fun pickUnit(label: String): Unit?
    }
}