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
        val context = foodName.context
        val formatter = SimpleDateFormat(context.getString(R.string.format_date), Locale.getDefault())
        val amount = shopPlan.food?.amount ?: 0.toDouble()
        val diff = shopPlan.amount
        val unitLabel = shopPlan.food?.unit?.label

        foodName.text = shopPlan.food?.name
        amountDiff.text = context.getString(R.string.format_amount_with_unit).format(diff, unitLabel)
        previousAmount.text = context.getString(R.string.format_amount).format(amount)
        date.text = formatter.format(shopPlan.date)
        completeButton.isChecked = false
    }
}