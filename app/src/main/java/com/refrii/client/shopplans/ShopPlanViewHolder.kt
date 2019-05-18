package com.refrii.client.shopplans

import android.view.View
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.refrii.client.R
import com.refrii.client.data.models.ShopPlan
import java.text.SimpleDateFormat
import java.util.*

class ShopPlanViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    @BindView(R.id.foodNameTextView)
    lateinit var foodName: TextView
    @BindView(R.id.previousAmountTextView)
    lateinit var previousAmount: TextView
    @BindView(R.id.amountDiffTextView)
    lateinit var amountDiff: TextView
    @BindView(R.id.dateTextView)
    lateinit var date: TextView
    @BindView(R.id.completeRadioButton)
    lateinit var completeButton: RadioButton

    init {
        ButterKnife.bind(this, view)
    }

    fun bind(shopPlan: ShopPlan) {
        val formatter = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
        val amount = shopPlan.food?.amount ?: 0.toDouble()
        val diff = shopPlan.amount
        val unitLabel = shopPlan.food?.unit?.label

        foodName.text = shopPlan.food?.name
        amountDiff.text = "$diff $unitLabel"
        previousAmount.text = amount.toString()
        date.text = formatter.format(shopPlan.date)
    }
}