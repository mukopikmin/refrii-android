package com.refrii.client.views.adapters

import android.content.Context
import android.support.design.widget.Snackbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast

import com.daimajia.swipe.SwipeLayout
import com.daimajia.swipe.adapters.BaseSwipeAdapter
import com.refrii.client.BasicCallback
import com.refrii.client.R
import com.refrii.client.RetrofitFactory
import com.refrii.client.models.Food
import com.refrii.client.services.FoodService

import java.text.SimpleDateFormat
import java.util.Collections

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Response

class FoodListAdapter(private val mContext: Context, private val mFoods: MutableList<Food>) : BaseSwipeAdapter() {
    private val mFoodListAdapter: FoodListAdapter

    init {
        mFoodListAdapter = this
        Collections.sort(mFoods)
    }

    fun add(food: Food) {
        this.mFoods.add(food)
    }

    fun remove(food: Food) {
        mFoods.remove(food)
    }

    override fun getSwipeLayoutResourceId(position: Int): Int {
        return R.id.foodListSwipeLayout
    }

    override fun generateView(position: Int, parent: ViewGroup): View {
        val v = LayoutInflater.from(mContext).inflate(R.layout.food_list_row, null)
        val swipeLayout = v.findViewById<View>(getSwipeLayoutResourceId(position)) as SwipeLayout
        swipeLayout.setOnDoubleClickListener { layout, surface -> Toast.makeText(mContext, "DoubleClick", Toast.LENGTH_SHORT).show() }

        v.findViewById<View>(R.id.incrementImageView).setOnClickListener { view ->
            val food = mFoods[position]
            food.amount = food.amount + 1
            updateFood(food, view)
        }
        v.findViewById<View>(R.id.decrementImageView).setOnClickListener { view ->
            val food = mFoods[position]
            food.amount = food.amount - 1
            updateFood(food, view)
        }
        return v
    }

    override fun fillValues(position: Int, convertView: View) {
        val food = mFoods[position]
        val formatter = SimpleDateFormat("yyyy/MM/dd")

        (convertView.findViewById<View>(R.id.nameFoodListTextView) as TextView).text = food.name
        (convertView.findViewById<View>(R.id.expirationDateFoodListTextView) as TextView).text = formatter.format(food.expirationDate)
        (convertView.findViewById<View>(R.id.amountFoodListTextView) as TextView).text = (food.amount.toString() + " " + food.unit!!.label).toString()
    }

    override fun getCount(): Int {
        return this.mFoods.size
    }

    override fun getItem(position: Int): Any {
        return this.mFoods[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    private fun updateFood(food: Food, view: View) {
        val body = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("amount", food.amount.toString())
                .build()
        val service = RetrofitFactory.getClient(FoodService::class.java, mContext)
        val call = service.updateFood(food.id, body)
        call.enqueue(object : BasicCallback<Food>(mContext) {
            override fun onResponse(call: Call<Food>, response: Response<Food>) {
                super.onResponse(call, response)

                val food = response.body()
                Snackbar.make(view, "Amount of " + food!!.name + " updated", Snackbar.LENGTH_LONG)
                        .setAction("Dismiss", null)
                        .show()
                mFoodListAdapter.notifyDataSetChanged()
            }

            override fun onFailure(call: Call<Food>, t: Throwable) {
                super.onFailure(call, t)

                Snackbar.make(view, t.message.toString(), Snackbar.LENGTH_LONG)
                        .setAction("Dismiss", null)
                        .show()
            }
        })
    }
}
