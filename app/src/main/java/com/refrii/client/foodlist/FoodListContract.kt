package com.refrii.client.foodlist

import com.refrii.client.data.api.models.Box
import com.refrii.client.data.api.models.Food

interface FoodListContract {

    interface View {
        fun clearBoxes()
        fun setBoxes(boxes: List<Box>?)
        fun setFoods(boxName: String?, foods: List<Food>?)
        fun showBoxInfo(box: Box)
        fun addFood(box: Box?)
        fun showFood(id: Int, box: Box?)
        fun onFoodUpdated(food: Food?)
        fun showProgressBar()
        fun hideProgressBar()
        fun showToast(message: String?)
        fun showSnackbar(message: String?)
        fun signOut()
        fun showBottomNavigation(food: Food)
        fun hideBottomNavigation()
        fun showConfirmDialog(food: Food?)
    }

    interface Presenter {
        fun takeView(view: View)
        fun getBox(): Box?
        fun getBoxInfo()
        fun pickBox(menuItemId: Int): Boolean
        fun getBoxes()
        fun incrementFood()
        fun decrementFood()
        fun removeFood()
        fun showFood()
        fun addFood()
        fun selectBox(box: Box)
        fun getExpiringFoods()
        fun deleteLocalData()
        fun selectFood(food: Food)
        fun confirmRemovingFood()
    }
}