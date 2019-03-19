package com.refrii.client.food

import com.refrii.client.data.api.models.Food
import com.refrii.client.data.api.models.Unit
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
        fun onUpdateCompleted()
    }

    interface Presenter {
        fun takeView(view: View)
        fun getFood(id: Int)
        fun getUnits(userId: Int)
        fun updateFood()
        fun editExpirationDate()
        fun updateName(name: String)
        fun updateAmount(amount: Double)
        fun updateNotice(notice: String)
        fun updateExpirationDate(date: Date)
    }
}