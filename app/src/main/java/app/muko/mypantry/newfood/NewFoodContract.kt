package app.muko.mypantry.newfood

import app.muko.mypantry.data.models.Box
import app.muko.mypantry.data.models.Food
import app.muko.mypantry.data.models.Unit

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
        fun getUnits(boxId: Int)
        fun getBox(id: Int)
        fun pickUnit(label: String): Unit?
        fun createFood(food: Food)
    }
}