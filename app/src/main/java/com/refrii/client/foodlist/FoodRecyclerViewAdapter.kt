package com.refrii.client.foodlist

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.refrii.client.R
import com.refrii.client.data.models.Food
import java.text.SimpleDateFormat
import java.util.*

class FoodRecyclerViewAdapter(private var mFoods: List<Food>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mOnClickListener: View.OnClickListener? = null
    private var mSelectedPosition: Int? = null

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
        val view = LayoutInflater.from(parent.context).inflate(R.layout.food_list_row, parent, false)

        return FoodViewHolder(view)
    }

    fun setFoods(foods: List<Food>) {
        mFoods = foods
        notifyDataSetChanged()
    }

    fun selectItem(position: Int) {
        if (isItemSelected()) {
            mSelectedPosition?.let {
                if (it == position) {
                    mSelectedPosition = null
                } else {
                    mSelectedPosition = position
                }
            }
        } else {
            mSelectedPosition = position
        }

        notifyDataSetChanged()
    }

    fun setOnClickListener(listener: View.OnClickListener) {
        mOnClickListener = listener
    }

    override fun getItemCount(): Int {
        return mFoods.size
    }

    fun getItemAtPosition(position: Int): Food {
        return mFoods[position]
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
