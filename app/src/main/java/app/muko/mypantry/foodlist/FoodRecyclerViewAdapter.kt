package app.muko.mypantry.foodlist

import android.graphics.Color
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import app.muko.mypantry.R
import app.muko.mypantry.data.models.Food
import com.ethanhua.skeleton.Skeleton
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*


class FoodRecyclerViewAdapter(
        private var mFoods: List<Food>,
        private val mUserId: Int
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mOnClickListener: View.OnClickListener? = null
    private var mSelectedPosition: Int? = null

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val food = mFoods[position]
        val formatter = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
        val amountWithUnit = "${String.format("%.2f", food.amount)} ${food.unit?.label}"

        (holder as FoodViewHolder).apply {
            val user = food.updatedUser
            name.text = food.name
            expirationDate.text = formatter.format(food.expirationDate)
            amount.text = amountWithUnit

            if (mUserId == user?.id) {
                lastUpdatedUserAvatarImageView.visibility = View.GONE
            } else {
                lastUpdatedUserAvatarImageView.visibility = View.VISIBLE

                user?.let {
                    if (user.avatarUrl.isNullOrEmpty()) {
                        val context = lastUpdatedUserAvatarImageView.context
                        val avatar = context.getDrawable(R.drawable.ic_outline_account_circle)

                        avatar?.let {
                            it.setTint(ContextCompat.getColor(context, android.R.color.darker_gray))
                            it.setTintMode(PorterDuff.Mode.SRC_IN)
                        }

                        lastUpdatedUserAvatarImageView.setImageResource(R.drawable.ic_outline_account_circle)
                    } else {
                        val skeleton = Skeleton.bind(lastUpdatedUserAvatarImageView)
                                .load(R.layout.skeleton_circle_image)
                                .duration(800)
                                .show()

                        Picasso.get()
                                .load(food.updatedUser?.avatarUrl)
                                .placeholder(R.drawable.ic_outline_account_circle)
                                .into(lastUpdatedUserAvatarImageView, object : Callback {
                                    override fun onSuccess() {
                                        skeleton.hide()
                                    }

                                    override fun onError(e: Exception?) {}
                                })
                    }
                }
            }

            if (food.notices.isNullOrEmpty()) {
                noticeCountView.visibility = View.GONE
            } else {
                noticeCountView.visibility = View.VISIBLE
                noticeCountTextView.text = food.notices?.size.toString()
            }

            constraintLayout.setBackgroundColor(Color.parseColor("#00000000"))
            mSelectedPosition?.let {
                if (it == position) {
                    constraintLayout.setBackgroundColor(Color.parseColor("#F5D0A9"))
                }
            }

            constraintLayout.setOnClickListener { mOnClickListener?.onClick(it as View) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_food, parent, false)

        return FoodViewHolder(view)
    }

    fun setFoods(foods: List<Food>) {
        mFoods = foods
        notifyDataSetChanged()
    }

    fun selectItem(position: Int) {
        mSelectedPosition = position

        notifyDataSetChanged()
    }

    fun setOnClickListener(listener: View.OnClickListener) {
        mOnClickListener = listener
    }

    override fun getItemCount(): Int {
        return mFoods.size
    }

    fun getItemAtPosition(position: Int): Food? {
        return if (position < 0) {
            null
        } else {
            mFoods[position]
        }
    }

    fun updateItem(food: Food) {
        val index = mFoods.indexOf(food)
        val foods = mFoods.toMutableList()

        foods[index] = food
        mFoods = foods.toList()

        notifyDataSetChanged()
    }

    fun isItemSelected(): Boolean {
        return mSelectedPosition != null
    }

    fun deselectItem() {
        mSelectedPosition = null

        notifyDataSetChanged()
    }

    fun getItemPosition(food: Food): Int {
        return mFoods.indexOfFirst { it.id == food.id }
    }
}
