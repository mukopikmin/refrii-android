package com.refrii.client.foodlist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter
import com.daimajia.swipe.implments.SwipeItemRecyclerMangerImpl
import com.refrii.client.R
import com.refrii.client.data.api.models.Food
import java.text.SimpleDateFormat
import java.util.*

class FoodRecyclerViewAdapter(private var mFoods: List<Food>) : RecyclerSwipeAdapter<FoodViewHolder>() {
    private var mItemManager = SwipeItemRecyclerMangerImpl(this)
//    private var mRealm: Realm

    var mClickListener: View.OnClickListener? = null
    var mLongClickListener: View.OnLongClickListener? = null
    var mIncrementClickListener: View.OnClickListener? = null
    var mDecrementClickListener: View.OnClickListener? = null

//    init {
//        Realm.init(context)
//        mRealm = RealmUtil.getInstance()
//    }

    override fun getSwipeLayoutResourceId(position: Int): Int {
        return R.id.swipeLayout
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
//        val id = mFoods[position].id
//        val food = mRealm.where(Food::class.java).equalTo("id", id).findFirst()
        val food = mFoods[position]
        val formatter = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())

        holder?.name?.text = food.name
        holder?.expirationDate?.text = formatter.format(food.expirationDate)
        holder?.amount?.text = "${food.amount} ${food.unit?.label}"

        holder?.swipeLayout?.surfaceView?.apply {
            setOnClickListener {
                mClickListener?.onClick(it.parent as View)
            }

            setOnLongClickListener {
                mLongClickListener?.onLongClick(it.parent as View)

                true
            }
        }

        holder?.increment?.setOnClickListener {
            mIncrementClickListener?.onClick(it.parent.parent as View)
        }

        holder?.decrement?.setOnClickListener {
            mDecrementClickListener?.onClick(it.parent.parent as View)
        }

        mItemManager.bindView(holder?.itemView, position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.food_list_row, parent, false)

        return FoodViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mFoods.size
    }

    fun getItemAtPosition(position: Int): Food {
        return mFoods[position]
    }

//    fun add(food: Food) {
//        mFoods.add(food)
//    }
//
//    fun removeFood(food: Food) {
//        mFoods.removeFood(food)
//    }

    companion object {
        private const val TAG = "FoodRecyclerAdapter"
    }
}
