package com.refrii.client.views.adapters

import android.content.Context
import android.support.design.widget.Snackbar
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.daimajia.swipe.SwipeLayout
import com.daimajia.swipe.adapters.BaseSwipeAdapter
import com.refrii.client.R
import com.refrii.client.models.Food
import com.refrii.client.services.FoodService
import com.refrii.client.services.RetrofitFactory
import io.realm.Realm
import io.realm.RealmConfiguration
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
    private var mRealm: Realm

    init {
        Collections.sort(mFoods)

        Realm.setDefaultConfiguration(RealmConfiguration.Builder(mContext).build())
        mRealm = Realm.getDefaultInstance()
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
            syncFood(food)
        }

        v.findViewById<View>(R.id.decrementImageView).setOnClickListener { view ->
            val food = mFoods[position]
            food.amount = food.amount - 1
            updateFood(food, view)
            syncFood(food)
        }

        return v
    }

    override fun fillValues(position: Int, convertView: View) {
        val food = mFoods[position]
        val formatter = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
        val nameFoodListTextView = convertView.findViewById<View>(R.id.nameFoodListTextView) as TextView
        val expirationDateFoodListTextView = convertView.findViewById<View>(R.id.expirationDateFoodListTextView) as TextView
        val amountFoodListTextView = convertView.findViewById<View>(R.id.amountFoodListTextView) as TextView

        nameFoodListTextView.text = food.name
        expirationDateFoodListTextView.text = formatter.format(food.expirationDate)
        amountFoodListTextView.text = food.amount.toString() + " " + food.unit!!.label
    }

    override fun getCount(): Int = this.mFoods.size

    override fun getItem(position: Int): Any = this.mFoods[position]

    override fun getItemId(position: Int): Long = position.toLong()

    private fun updateFood(food: Food, view: View) {
        mRealm.executeTransaction { mRealm.copyToRealmOrUpdate(food) }
        mFoodListAdapter.notifyDataSetChanged()

        Snackbar.make(view, "Updated successfully.", Snackbar.LENGTH_LONG).show()
    }

    private fun syncFood(food: Food) {
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
                        Toast.makeText(mContext, e.message, Toast.LENGTH_LONG).show()
                    }

                    override fun onCompleted() {
                        Log.d(TAG, "Update completed.")
                    }

                    override fun onNext(t: Food) {
                        mFoodListAdapter.notifyDataSetChanged()
                    }
                })
    }

    companion object {
        private const val TAG = "FoodListAdapter"
    }
}
