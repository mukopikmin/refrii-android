package app.muko.mypantry.foodlist

import android.graphics.Color
import android.graphics.PorterDuff
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import app.muko.mypantry.R
import app.muko.mypantry.data.models.Food
import butterknife.BindView
import butterknife.ButterKnife
import com.ethanhua.skeleton.Skeleton
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*

class FoodViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {

    @BindView(R.id.constraintLayout)
    lateinit var mContainer: View
    @BindView(R.id.nameFoodListTextView)
    lateinit var mName: TextView
    @BindView(R.id.expirationDateFoodListTextView)
    lateinit var mExpirationDate: TextView
    @BindView(R.id.amountFoodListTextView)
    lateinit var mAmount: TextView
    @BindView(R.id.lastUpdatedUserAvatarImageView)
    lateinit var mUpdatedUserAvatar: ImageView
    @BindView(R.id.noticeCountView)
    lateinit var mNoticeIcon: View
    @BindView(R.id.noticeCountTextView)
    lateinit var mNoticeCount: TextView

    private var mImageInjected = false

    init {
        ButterKnife.bind(this, view)
    }

    fun bind(food: Food, myselfId: Int, selectedPosition: Int?, onClickListener: View.OnClickListener?) {
        val formatter = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
        val amountWithUnit = "${String.format("%.2f", food.amount)} ${food.unit?.label}"
        val user = food.updatedUser

        mName.text = food.name
        mExpirationDate.text = formatter.format(food.expirationDate)
        mAmount.text = amountWithUnit

        if (myselfId == user?.id) {
            mUpdatedUserAvatar.visibility = View.GONE
        } else {
            mUpdatedUserAvatar.visibility = View.VISIBLE

            user?.let {
                if (user.avatarUrl.isNullOrEmpty()) {
                    val context = mUpdatedUserAvatar.context
                    val avatar = context.getDrawable(R.drawable.ic_outline_account_circle)

                    avatar?.let {
                        it.setTint(ContextCompat.getColor(context, android.R.color.darker_gray))
                        it.setTintMode(PorterDuff.Mode.SRC_IN)
                    }

                    mUpdatedUserAvatar.setImageResource(R.drawable.ic_outline_account_circle)
                } else if (!mImageInjected) {
                    val skeleton = Skeleton.bind(mUpdatedUserAvatar)
                            .load(R.layout.skeleton_circle_image)
                            .duration(800)
                            .show()

                    Picasso.get()
                            .load(user.avatarUrl)
                            .into(mUpdatedUserAvatar, object : Callback {
                                override fun onSuccess() {
                                    mImageInjected = true
                                    skeleton.hide()
                                }

                                override fun onError(e: Exception?) {
                                    skeleton.hide()
                                }
                            })
                }
            }
        }

        if (food.notices.isNullOrEmpty()) {
            mNoticeIcon.visibility = View.GONE
        } else {
            mNoticeIcon.visibility = View.VISIBLE
            mNoticeCount.text = food.notices?.size.toString()
        }

        mContainer.setBackgroundColor(Color.parseColor("#00000000"))
        if (selectedPosition == adapterPosition) {
            mContainer.setBackgroundColor(Color.parseColor("#F5D0A9"))
        }

        mContainer.setOnClickListener { onClickListener?.onClick(it as View) }
    }
}