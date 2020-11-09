package app.muko.mypantry.ui.activities.shopplans

import app.muko.mypantry.data.models.ShopPlan

interface ShopPlansContract {

    interface View {
        fun setShopPlans(shopPlans: List<ShopPlan>)
        fun showToast(message: String?)
        fun showSnackBar(message: String?)
    }

    interface Presenter {
        fun init(view: View)
        fun getShopPlans()
        fun completeShopPlan(shopPlan: ShopPlan)
    }
}