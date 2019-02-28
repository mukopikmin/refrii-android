package com.refrii.client.foodlist

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.refrii.client.R
import com.refrii.client.data.api.models.Food
import java.text.SimpleDateFormat
import java.util.*

class FoodRecyclerViewAdapter(private var mFoods: List<Food>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
//    private var mItemManager = SwipeItemRecyclerMangerImpl(this)
//    private var mRealm: Realm

    var mEditClickListener: View.OnClickListener? = null
    var mSubmitClickListener: View.OnClickListener? = null
    var mIncrementClickListener: View.OnClickListener? = null
    var mDecrementClickListener: View.OnClickListener? = null
    var mSelectedPosition: Int = UNSELECTED_INDEX

//    init {
//        Realm.init(context)
//        mRealm = RealmUtil.getInstance()
//    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//        val id = mFoods[position].id
//        val food = mRealm.where(Food::class.java).equalTo("id", id).findFirst()
        val food = mFoods[position]
        val formatter = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
        val amountWithUnit = "${food.amount} ${food.unit?.label}"

        (holder as FoodViewHolder).apply {
            name.text = food.name
            expirationDate.text = formatter.format(food.expirationDate)
            amount.text = amountWithUnit
            editAmountTextView.text = amountWithUnit

            if (position == mSelectedPosition) {
                menu.visibility = View.VISIBLE
            } else {
                menu.visibility = View.GONE
            }

            holder.constraintLayout.setOnClickListener {
                mSelectedPosition = position
                notifyDataSetChanged()
            }

            incrementButton.setOnClickListener { mIncrementClickListener?.onClick(it.parent.parent.parent as View) }
            decrementButton.setOnClickListener { mDecrementClickListener?.onClick(it.parent.parent.parent as View) }
            submitButton.setOnClickListener { mSubmitClickListener?.onClick(it) }
            editButton.setOnClickListener { mEditClickListener?.onClick(it.parent.parent as View) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.food_list_row, parent, false)

        return FoodViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mFoods.size
    }

    fun getItemAtPosition(position: Int): Food {
        return mFoods[position]
    }

    fun isItemSelected(): Boolean {
        return mSelectedPosition != UNSELECTED_INDEX
    }

    fun deselectItem() {
        mSelectedPosition = UNSELECTED_INDEX
        notifyDataSetChanged()
    }

//    fun add(food: Food) {
//        mFoods.add(food)
//    }
//
//    fun removeFood(food: Food) {
//        mFoods.removeFood(food)
//    }

    companion object {
        private const val UNSELECTED_INDEX = -1
    }
}
