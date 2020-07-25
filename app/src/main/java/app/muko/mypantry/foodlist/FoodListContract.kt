package app.muko.mypantry.foodlist

import app.muko.mypantry.data.models.Box
import app.muko.mypantry.data.models.Food

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
        fun showBottomNavigation(food: Food)
        fun hideBottomNavigation()
        fun showConfirmDialog(food: Food?)
        fun setEmptyMessage(foods: List<Food>?)
        fun savePushToken(token: String)
        fun deselectFood()
        fun showNotices(food: Food)
        fun setNoBoxesMessage()
        fun onBoxCreated()
    }

    interface Presenter {
        fun takeView(view: View)
        fun getBox(): Box?
        fun getBoxInfo()
        fun createBox(name: String, notice: String)
        fun pickBox(menuItemId: Int): Boolean
        fun getBoxes()
        fun incrementFood()
        fun decrementFood()
        fun removeFood()
        fun showFood()
        fun addFood()
        fun selectBox(box: Box)
        fun getExpiringFoods()
        fun selectFood(food: Food)
        fun deselectFood()
        fun isFoodSelected(food: Food): Boolean
        fun confirmRemovingFood()
        fun registerPushToken(userId: Int, token: String)
        fun showNotices()
    }
}