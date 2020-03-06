package app.muko.mypantry.shopplans

import app.muko.mypantry.data.models.ShopPlan

interface ShopPlansContract {

    interface View {
        fun setShopPlans(shopPlans: List<ShopPlan>?)
        fun showToast(message: String?)
        fun showSnackBar(message: String?)
    }

    interface Presenter {
        fun takeView(view: View)
        fun getShopPlans()
        fun completeShopPlan(shopPlan: ShopPlan)
    }
}