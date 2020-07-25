package app.muko.mypantry.food

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import app.muko.mypantry.data.models.Food
import app.muko.mypantry.data.models.ShopPlan
import app.muko.mypantry.data.models.Unit
import java.util.*

interface FoodContract {

    interface View {
        fun showEditDateDialog()
        fun showProgressBar()
        fun hideProgressBar()
        fun showSnackbar(message: String?)
        fun showToast(message: String?)
        fun setUnits(units: List<Unit>?)
        fun onUpdateCompleted(food: Food?)
        fun setShopPlans(shopPlans: List<ShopPlan>?)
        fun showCreateShopPlanDialog()
        fun onCompletedCompleteShopPlan(shopPlan: ShopPlan?)
    }

    interface Presenter {
        fun takeView(view: View)
        fun getLiveData(id: Int): LiveData<Food>
        fun getFood(id: Int)
        fun getUnits(boxId: Int)
        fun updateFood(id: Int, name: String?, amount: Double?, expirationDate: Date?, image: Bitmap?, boxId: Int?, unitId: Int?)
        fun getShopPlans(id: Int)
        fun createShopPlan(amount: Double, date: Date, foodId: Int)
        fun completeShopPlan(shopPlan: ShopPlan)
    }
}