package com.refrii.client.views.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter
import com.refrii.client.R
import com.refrii.client.RealmUtil
import com.refrii.client.models.Food
import com.refrii.client.services.FoodService
import com.refrii.client.services.RetrofitFactory
import io.realm.Realm
import okhttp3.MultipartBody
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers.io
import java.text.SimpleDateFormat
import java.util.*

class FoodRecyclerViewAdapter(
        private val context: Context,
        var foods: MutableList<Food>) : RecyclerSwipeAdapter<FoodViewHolder>() {

    private var mRealm: Realm
    private var mClickListener: View.OnClickListener? = null
    private var mLongClickListener: View.OnLongClickListener? = null
    private var mIncrementClickListener: View.OnClickListener? = null
    private var mDecrementClickListener: View.OnClickListener? = null
    private val self = this

    init {
        Realm.init(context)
        mRealm = RealmUtil.getInstance()
    }

    override fun getSwipeLayoutResourceId(position: Int): Int {
        return R.id.swipeLayout
    }

    override fun onBindViewHolder(holder: FoodViewHolder?, position: Int) {
        val id = foods[position].id
        val food = mRealm.where(Food::class.java).equalTo("id", id).findFirst()
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
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): FoodViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.food_list_row, parent, false)

        return FoodViewHolder(view)
    }

    override fun getItemCount(): Int {
        return foods.size
    }

    fun setOnItemClickListener(listener: View.OnClickListener) {
        mClickListener = listener
    }

    fun setOnItemLongClickListener(listener: View.OnLongClickListener) {
        mLongClickListener = listener
    }

    fun setOnIncrementClickListener(listener: View.OnClickListener) {
        mIncrementClickListener = listener
        (self as RecyclerSwipeAdapter<FoodViewHolder>).notifyDataSetChanged()
    }

    fun setOnDecrementClickListener(listener: View.OnClickListener) {
        mDecrementClickListener = listener
    }

    private fun syncFood(food: Food) {
        val body = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("amount", food.amount.toString())
                .build()

        RetrofitFactory.getClient(FoodService::class.java, context)
                .updateFood(food.id, body)
                .subscribeOn(io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Subscriber<Food>() {
                    override fun onError(e: Throwable) {
                        Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                    }

                    override fun onCompleted() {
                        Log.d(TAG, "Update completed.")
                    }

                    override fun onNext(t: Food) {
//                        this.notifyDataSetChanged()
                    }
                })
    }

    fun getItemAtPosition(position: Int): Food {
        return foods[position]
    }

    fun add(food: Food) {
        foods.add(food)
    }

    fun remove(food: Food) {
        foods.remove(food)
    }

    companion object {
        private const val TAG = "FoodRecyclerAdapter"
    }
}
