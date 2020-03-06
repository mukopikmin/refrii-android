package app.muko.mypantry.foodlist

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import app.muko.mypantry.R

class FoodViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
    val constraintLayout: ConstraintLayout = view.findViewById(R.id.constraintLayout)
    val name: TextView = view.findViewById(R.id.nameFoodListTextView)
    val expirationDate: TextView = view.findViewById(R.id.expirationDateFoodListTextView)
    val amount: TextView = view.findViewById(R.id.amountFoodListTextView)
    val lastUpdatedUserAvatarImageView: ImageView = view.findViewById(R.id.lastUpdatedUserAvatarImageView)
    val noticeCountView: View = view.findViewById(R.id.noticeCountView)
    val noticeCountTextView: TextView = view.findViewById(R.id.noticeCountTextView)
}