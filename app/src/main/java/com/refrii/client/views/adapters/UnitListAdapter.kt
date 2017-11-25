package com.refrii.client.views.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.refrii.client.R
import com.refrii.client.models.Unit

class UnitListAdapter(
        context: Context,
        private val units: List<Unit>): BaseAdapter() {

    private val mLayoutInflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int = units.size

    override fun getItem(i: Int): Any = units[i]

    override fun getItemId(i: Int): Long = units[i].id.toLong()

    override fun getView(i: Int, _view: View?, viewGroup: ViewGroup): View {
        val view = mLayoutInflater.inflate(R.layout.unit_list_row, viewGroup, false)
        val labelTextView = view.findViewById<TextView>(R.id.labelTextView)
        val unit: Unit = units[i]

        labelTextView.text = unit.label

        return view
    }
}