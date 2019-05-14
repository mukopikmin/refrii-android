package com.refrii.client.food

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.refrii.client.R

class ShopPlanViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val amountDiff: TextView = view.findViewById(R.id.amountDiffTextView)
    val afterAmount: TextView = view.findViewById(R.id.afterAmountTextView)
    val date: TextView = view.findViewById(R.id.dateTextView)
    val completeButton: View = view.findViewById(R.id.completeImageView)
}