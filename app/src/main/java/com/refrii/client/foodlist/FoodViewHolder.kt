package com.refrii.client.foodlist

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.daimajia.swipe.SwipeLayout
import com.refrii.client.R

class FoodViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val swipeLayout: SwipeLayout = view.findViewById(R.id.swipeLayout)
    val name: TextView = view.findViewById(R.id.nameFoodListTextView)
    val expirationDate: TextView = view.findViewById(R.id.expirationDateFoodListTextView)
    val amount: TextView = view.findViewById(R.id.amountFoodListTextView)
    val increment: ImageView = view.findViewById(R.id.incrementImageView)
    val decrement: ImageView = view.findViewById(R.id.decrementImageView)
}