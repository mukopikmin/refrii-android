package com.refrii.client.foodlist

import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import com.refrii.client.R

class FoodViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val constraintLayout: ConstraintLayout = view.findViewById(R.id.constraintLayout)
    val name: TextView = view.findViewById(R.id.nameFoodListTextView)
    val expirationDate: TextView = view.findViewById(R.id.expirationDateFoodListTextView)
    val amount: TextView = view.findViewById(R.id.amountFoodListTextView)
    val menu: ConstraintLayout = view.findViewById(R.id.menu)
    val editAmountTextView: TextView = view.findViewById(R.id.editAmountTextView)
    val incrementButton: ImageButton = view.findViewById(R.id.incrementButton)
    val decrementButton: ImageButton = view.findViewById(R.id.decrementButton)
    val deleteButton: ImageButton = view.findViewById(R.id.deleteButton)
    val editButton: ImageButton = view.findViewById(R.id.editButton)
}