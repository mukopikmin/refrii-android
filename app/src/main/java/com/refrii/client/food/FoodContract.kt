package com.refrii.client.food

import com.refrii.client.data.api.models.Box
import com.refrii.client.data.api.models.Food
import java.util.*

interface FoodContract {

    interface View {
        fun setFood(food: Food?, box: Box?)
        fun onBeforeEdit()
        fun onEdited()
        fun showEditNameDialog(name: String?)
        fun showEditAmountDialog(amount: Double?)
        fun showEditNoticeDialog(notice: String?)
        fun showEditDateDialog(date: Date?)
        fun showProgressBar()
        fun hideProgressBar()
        fun showSnackbar(message: String?)
        fun showToast(message: String?)
    }

    interface Presenter {
        fun takeView(view: View)
        fun getFood(id: Int)
        fun getBox(id: Int)
        fun updateFood()
        fun editName()
        fun editAmount()
        fun editNotice()
        fun editExpirationDate()
        fun updateName(name: String)
        fun updateAmount(amount: Double)
        fun updateNotice(notice: String)
        fun updateExpirationDate(date: Date)
    }
}