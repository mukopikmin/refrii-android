package com.refrii.client.food

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.refrii.client.R
import com.refrii.client.data.models.Food
import com.refrii.client.data.models.ShopPlan

class ShopPlanRecyclerViewAdapter(
        private var mShopPlans: List<ShopPlan>,
        private val mFood: Food
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mOnClickListener: View.OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_shop_plan_compact, parent, false)

        return ShopPlanViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mShopPlans.size
    }

    override fun onBindViewHolder(_holder: RecyclerView.ViewHolder, position: Int) {
        val shopPlan = mShopPlans[position]
        val holder = _holder as ShopPlanViewHolder

        holder.bind(shopPlan, mFood)
        holder.completeButton.setOnClickListener { mOnClickListener?.onClick(it.parent as View) }
    }

    fun setShopPlans(shopPlans: List<ShopPlan>) {
        mShopPlans = shopPlans

        notifyDataSetChanged()
    }

    fun setOnClickListener(listener: View.OnClickListener) {
        mOnClickListener = listener
    }

    fun getItemAtPosition(position: Int): ShopPlan {
        return mShopPlans[position]
    }

    fun completeShopPlan(shopPlan: ShopPlan) {
        mShopPlans = mShopPlans.filter { it.id != shopPlan.id }

        notifyDataSetChanged()
    }
}