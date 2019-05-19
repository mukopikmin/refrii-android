package com.refrii.client.food

import android.view.View
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.refrii.client.R
import com.refrii.client.data.models.Food
import com.refrii.client.data.models.ShopPlan
import java.text.SimpleDateFormat
import java.util.*

class ShopPlanViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    @BindView(R.id.amountDiffTextView)
    lateinit var amountDiff: TextView
    @BindView(R.id.dateTextView)
    lateinit var date: TextView
    @BindView(R.id.completeRadioButton)
    lateinit var completeButton: RadioButton

    init {
        ButterKnife.bind(this, view)
    }

    fun bind(shopPlan: ShopPlan, food: Food) {
        val context = amountDiff.context
        val formatter = SimpleDateFormat(context.getString(R.string.date_format), Locale.getDefault())
        val unitLabel = food.unit?.label
        val diff = shopPlan.amount

        amountDiff.text = context.getString(R.string.amount_with_unit).format(diff, unitLabel)
        date.text = formatter.format(shopPlan.date)
        completeButton.isChecked = false
    }
}