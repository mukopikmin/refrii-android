package com.refrii.client.shopplans

import com.refrii.client.data.models.ShopPlan

interface ShopPlansContract {

    interface View {
        fun setShopPlans(shopPlans: List<ShopPlan>?)
    }

    interface Presenter {
        fun takeView(view: View)
        fun getShopPlans()
    }
}