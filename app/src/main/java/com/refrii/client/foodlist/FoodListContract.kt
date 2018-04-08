package com.refrii.client.foodlist

import com.refrii.client.data.api.models.Box
import com.refrii.client.data.api.models.Food

interface FoodListContract {

    interface View {
        fun setBoxes(boxes: List<Box>?)
        fun setFoods(box: Box?, foods: List<Food>?)
        fun showBoxInfo(box: Box)
        fun addFood(box: Box?)
        fun showFood(id: Int, box: Box?)
        fun showOptionsDialog(food: Food?)
        fun onFoodUpdated()
        fun showProgressBar()
        fun hideProgressBar()
        fun showToast(message: String?)
        fun showSnackbar(message: String?)
    }

    interface Presenter {
        fun takeView(view: View)
        fun getBoxInfo()
        fun pickBox(menuItemId: Int): Boolean
        fun getBoxes()
        fun incrementFood(food: Food)
        fun decrementFood(food: Food)
        fun removeFood(id: Int)
        fun showFood(id: Int)
        fun addFood()
        fun selectBox(box: Box)
    }
}