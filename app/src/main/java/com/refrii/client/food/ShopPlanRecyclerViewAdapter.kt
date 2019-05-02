package com.refrii.client.food

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.refrii.client.R
import com.refrii.client.data.models.ShopPlan
import com.refrii.client.data.models.Unit
import java.text.SimpleDateFormat
import java.util.*

class ShopPlanRecyclerViewAdapter(private var mShopPlans: List<ShopPlan>, private val unit: Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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
            amountText.text = "${shopPlan.amount} ${unit.label}"
            dateText.text = formatter.format(shopPlan.date)
        }
    }

    fun setShopPlans(shopPlans: List<ShopPlan>) {
        mShopPlans = shopPlans

        notifyDataSetChanged()
    }
}