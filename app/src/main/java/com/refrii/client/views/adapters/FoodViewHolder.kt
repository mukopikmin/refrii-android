package com.refrii.client.views.adapters

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.refrii.client.R

class FoodViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    var name = view.findViewById<TextView>(R.id.nameFoodListTextView)
    var expirationDate = view.findViewById<TextView>(R.id.expirationDateFoodListTextView)
    var amount = view.findViewById<TextView>(R.id.amountFoodListTextView)
}