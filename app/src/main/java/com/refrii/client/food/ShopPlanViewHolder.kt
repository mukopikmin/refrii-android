package com.refrii.client.food

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.refrii.client.R

class ShopPlanViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val amountText: TextView = view.findViewById(R.id.amountTextView)
    val dateText: TextView = view.findViewById(R.id.dateTextView)
}