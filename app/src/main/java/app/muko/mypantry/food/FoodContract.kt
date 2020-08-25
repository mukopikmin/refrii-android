package app.muko.mypantry.food

import app.muko.mypantry.data.models.Food
import app.muko.mypantry.data.models.ShopPlan
import java.io.File

interface FoodContract {

    interface View {
        fun setFood(food: Food?)
        fun setShopPlans(food: Food, shopPlans: List<ShopPlan>)
        fun showEditDateDialog()
        fun showProgressBar()
        fun hideProgressBar()
        fun showSnackbar(message: String?)
        fun showToast(message: String?)
        fun onUpdateCompleted(food: Food?)
        fun showCreateShopPlanDialog()
        fun onCompletedCompleteShopPlan(shopPlan: ShopPlan?)
    }

    interface Presenter {
        fun init(view: View, foodId: Int)
        fun terminate()
        fun getFood(id: Int)
        fun getUnits(boxId: Int)
        fun updateFood(food: Food, imageFile: File?)
        fun getShopPlans(id: Int)
        fun createShopPlan(shopPlan: ShopPlan)
        fun completeShopPlan(shopPlan: ShopPlan)
    }
}