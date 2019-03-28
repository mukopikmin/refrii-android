package com.refrii.client.foodlist

import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.refrii.client.R
import com.refrii.client.data.api.models.Food
import java.text.SimpleDateFormat
import java.util.*

class FoodRecyclerViewAdapter(private var mFoods: List<Food>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mOnClickListener: View.OnClickListener? = null
    private var mSelectedPosition: Int? = null

    fun setFoods(foods: List<Food>) {
        mFoods = foods
        notifyDataSetChanged()
    }

    fun select(position: Int) {
        if (mSelectedPosition == null) {
            mSelectedPosition = position
        } else {
            mSelectedPosition?.let {
                if (it == position) {
                    mSelectedPosition = null
                } else {
                    mSelectedPosition = position
                }
            }
        }

        notifyDataSetChanged()
    }

    fun setOnClickListener(listener: View.OnClickListener) {
        mOnClickListener = listener
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val food = mFoods[position]
        val formatter = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
        val amountWithUnit = "${food.amount} ${food.unit?.label}"

        (holder as FoodViewHolder).apply {
            name.text = food.name
            expirationDate.text = formatter.format(food.expirationDate)
            amount.text = amountWithUnit

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
        return mSelectedPosition != null
    }

    fun deselectItem() {
        mSelectedPosition = null
        notifyDataSetChanged()
    }
}
