package com.refrii.client.food

import com.refrii.client.data.models.Food
import com.refrii.client.data.models.Unit
import java.util.*

interface FoodContract {

    interface View {
        fun setFood(food: Food?)
        fun showEditDateDialog(date: Date?)
        fun showProgressBar()
        fun hideProgressBar()
        fun showSnackbar(message: String?)
        fun showToast(message: String?)
        fun setUnits(units: List<Unit>?)
        fun setExpirationDate(date: Date?)
        fun onUpdateCompleted(food: Food?)
        fun setSelectedUnit(id: Int?)
    }

    interface Presenter {
        fun takeView(view: View)
        fun getFood(id: Int)
        fun getUnits(boxId: Int)
        fun updateFood()
        fun editExpirationDate()
        fun updateName(name: String)
        fun updateAmount(amount: Double)
        fun updateNotice(notice: String)
        fun updateExpirationDate(date: Date)
        fun selectUnit(id: Int)
    }
}