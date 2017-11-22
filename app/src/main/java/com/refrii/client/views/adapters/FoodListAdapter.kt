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
import com.refrii.client.R
import com.refrii.client.factories.RetrofitFactory
import com.refrii.client.models.Food
import com.refrii.client.services.FoodService
import okhttp3.MultipartBody
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*

class FoodListAdapter(
        private val mContext: Context,
        private val mFoods: MutableList<Food>) : BaseSwipeAdapter() {

    private val mFoodListAdapter: FoodListAdapter = this

    init {
        Collections.sort(mFoods)
    }

    fun add(food: Food) {
        this.mFoods.add(food)
    }

    fun remove(food: Food) {
        mFoods.remove(food)
    }

    override fun getSwipeLayoutResourceId(position: Int): Int = R.id.foodListSwipeLayout

    override fun generateView(position: Int, parent: ViewGroup): View {
        val v = LayoutInflater.from(mContext).inflate(R.layout.food_list_row, null)
        val swipeLayout = v.findViewById<View>(getSwipeLayoutResourceId(position)) as SwipeLayout

        swipeLayout.setOnDoubleClickListener { _, _ -> Toast.makeText(mContext, "DoubleClick", Toast.LENGTH_SHORT).show() }

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
        val formatter = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())

        (convertView.findViewById<View>(R.id.nameFoodListTextView) as TextView).text = food.name
        (convertView.findViewById<View>(R.id.expirationDateFoodListTextView) as TextView).text = formatter.format(food.expirationDate)
        (convertView.findViewById<View>(R.id.amountFoodListTextView) as TextView).text = (food.amount.toString() + " " + food.unit!!.label).toString()
    }

    override fun getCount(): Int = this.mFoods.size

    override fun getItem(position: Int): Any = this.mFoods[position]

    override fun getItemId(position: Int): Long = position.toLong()

    private fun updateFood(food: Food, view: View) {
        val body = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("amount", food.amount.toString())
                .build()

        RetrofitFactory.getClient(FoodService::class.java, mContext)
                .updateFood(food.id, body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object: Subscriber<Food>() {
                    override fun onError(e: Throwable) {
                        Snackbar.make(view, e.message.toString(), Snackbar.LENGTH_LONG)
                                .setAction("Dismiss", null)
                                .show()
                    }

                    override fun onCompleted() {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onNext(t: Food) {
                        Snackbar.make(view, "Amount of " + t.name + " updated", Snackbar.LENGTH_LONG)
                                .setAction("Dismiss", null)
                                .show()
                        mFoodListAdapter.notifyDataSetChanged()
                    }
                })
    }
}
