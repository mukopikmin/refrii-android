package com.refrii.client.shopplans

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.refrii.client.R
import com.refrii.client.data.models.ShopPlan
import java.text.SimpleDateFormat
import java.util.*

class ShopPlansRecyclerViewAdapter(private var mShopPlans: List<ShopPlan>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_shop_plan, parent, false)

        return ShopPlanViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mShopPlans.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val shopPlan = mShopPlans[position]
        val formatter = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())

        (holder as ShopPlanViewHolder).apply {
            val amount = shopPlan.food?.amount ?: 0.toDouble()
            val diff = shopPlan.amount
            val unitLabel = shopPlan.food?.unit?.label

            foodName.text = shopPlan.food?.name
            amountDiff.text = "$diff $unitLabel"
            previousAmount.text = "$amount $unitLabel"
            afterAmount.text = "${amount + diff} $unitLabel"
            date.text = formatter.format(shopPlan.date)
        }
    }

    fun setShopPlans(shopPlans: List<ShopPlan>) {
        mShopPlans = shopPlans
    }
}