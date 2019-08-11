package com.refrii.client.foodlist

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.refrii.client.R

class FoodViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
    val constraintLayout: ConstraintLayout = view.findViewById(R.id.constraintLayout)
    val name: TextView = view.findViewById(R.id.nameFoodListTextView)
    val expirationDate: TextView = view.findViewById(R.id.expirationDateFoodListTextView)
    val amount: TextView = view.findViewById(R.id.amountFoodListTextView)
    val lastUpdateUserView: View = view.findViewById(R.id.lastUpdatedUserView)
    val lastUpdatedUserNameTextView: TextView = view.findViewById(R.id.lastUpdatedUserNameTextView)
    val lastUpdatedAtTextView: TextView = view.findViewById(R.id.lastUpdatedAtTextView)
    val lastUpdatedUserAvatarImageView: ImageView = view.findViewById(R.id.lastUpdatedUserAvatarImageView)
}