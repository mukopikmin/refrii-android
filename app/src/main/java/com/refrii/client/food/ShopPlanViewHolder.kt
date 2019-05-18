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
        val formatter = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
        val unitLabel = food.unit?.label
        val diff = shopPlan.amount

        amountDiff.text = arrayOf(diff, unitLabel).joinToString(" ")
        date.text = formatter.format(shopPlan.date)
    }
}