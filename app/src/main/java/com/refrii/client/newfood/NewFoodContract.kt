package com.refrii.client.newfood

import com.refrii.client.data.api.models.Food
import com.refrii.client.data.api.models.Unit
import java.util.*

interface NewFoodContract {

    interface View {
        fun setUnits(units: List<Unit>?)
        fun createCompleted(food: Food?)
        fun showProgressBar()
        fun hideProgressBar()
        fun showToast(message: String?)
    }

    interface Presenter {
        fun takeView(view: View)
        fun createFood(name: String, notice: String, amount: Double, unit: Unit?, expirationDate: Date)
        fun getUnits(userId: Int)
        fun getBox(id: Int)
        fun pickUnit(label: String): Unit?
    }
}