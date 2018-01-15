package com.refrii.client.views.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.refrii.client.R
import com.refrii.client.models.Food
import java.text.SimpleDateFormat
import java.util.*

class FoodRecyclerViewAdapter(val foods: MutableList<Food>) : RecyclerView.Adapter<FoodViewHolder>() {

    var onItemClickListener: (Food) -> Unit = {}
    var onItemLongClickListener: (Food) -> Unit = {}

    override fun onBindViewHolder(holder: FoodViewHolder?, position: Int) {
        val food = foods[position]
        val formatter = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())

        holder?.name?.text = food.name
        holder?.expirationDate?.text = formatter.format(food.expirationDate)
        holder?.amount?.text = "${food.amount} ${food.unit?.label}"
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): FoodViewHolder {
        val inflate = LayoutInflater.from(parent?.context).inflate(R.layout.food_list_row, parent, false)
        val holder = FoodViewHolder(inflate)

        holder.itemView.setOnClickListener {
            val position = holder.adapterPosition
            val food = foods[position]

            onItemClickListener(food)
        }

        holder.itemView.setOnLongClickListener {
            val position = holder.adapterPosition
            val food = foods[position]

            onItemLongClickListener(food)
            true
        }

        return holder
    }

    override fun getItemCount(): Int {
        return foods.size
    }

    fun restore(food: Food) {
        add(food)
        notifyDataSetChanged()
    }

    fun add(food: Food) {
        foods.add(food)
    }

    fun remove(food: Food) {
        foods.remove(food)
    }
}
