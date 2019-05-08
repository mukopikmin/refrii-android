package com.refrii.client.shopplans

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.refrii.client.R

class ShopPlanViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val foodName: TextView = view.findViewById(R.id.foodNameTextView)
    val previousAmount: TextView = view.findViewById(R.id.previousAmountTextView)
    val afterAmount: TextView = view.findViewById(R.id.afterAmountTextView)
    val amountDiff: TextView = view.findViewById(R.id.amountDiffTextView)
    val date: TextView = view.findViewById(R.id.dateTextView)
}