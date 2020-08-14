package app.muko.mypantry.food

import app.muko.mypantry.data.models.Food
import app.muko.mypantry.data.models.ShopPlan

interface FoodContract {

    interface View {
        fun showEditDateDialog()
        fun showProgressBar()
        fun hideProgressBar()
        fun showSnackbar(message: String?)
        fun showToast(message: String?)

        //        fun setUnits()
        fun onUpdateCompleted(food: Food?)

        //        fun setShopPlans(shopPlans: List<ShopPlan>?)
        fun showCreateShopPlanDialog()
        fun onCompletedCompleteShopPlan(shopPlan: ShopPlan?)
    }

    interface Presenter {
        fun takeView(view: View)
        fun initLiveData(foodId: Int)

        //        fun getFoodLiveData(id: Int): LiveData<Food>
//        fun getUnitsLiveData(): LiveData<List<Unit>>
//        fun getShopPlansLiveData(foodId: Int): LiveData<List<ShopPlan>>
        fun getFood(id: Int)
        fun getUnits(boxId: Int)
        fun updateFood(food: Food)
        fun getShopPlans(id: Int)
        fun createShopPlan(shopPlan: ShopPlan)
        fun completeShopPlan(shopPlan: ShopPlan)
    }
}