package app.muko.mypantry.ui.fragments.foodlist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.muko.mypantry.R
import app.muko.mypantry.data.models.Food


class FoodRecyclerViewAdapter(
        private var mFoods: List<Food>,
        private val mUserId: Int
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mOnClickListener: View.OnClickListener? = null
    private var mSelectedPosition: Int? = null

    override fun onBindViewHolder(_holder: RecyclerView.ViewHolder, position: Int) {
        val food = mFoods[position]
        val holder = _holder as FoodViewHolder

        holder.bind(food, mUserId, mSelectedPosition, mOnClickListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_food, parent, false)

        return FoodViewHolder(view)
    }

    fun select(food: Food?) {
        if (food==null) {
            mSelectedPosition=null
        } else {
            mSelectedPosition=mFoods.map{it.id}.indexOf(food.id)
        }
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
