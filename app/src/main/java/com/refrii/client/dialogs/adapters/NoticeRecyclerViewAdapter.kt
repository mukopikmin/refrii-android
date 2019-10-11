package com.refrii.client.dialogs.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.refrii.client.R
import java.text.SimpleDateFormat
import java.util.*

class NoticeRecyclerViewAdapter(
        private val mTexts: List<String>,
        private val mTimestamps: List<Date>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_notice, parent, false)

        return NoticeViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mTexts.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val formatter = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault())
        val text = mTexts[position]
        val timestamp = formatter.format(mTimestamps[position])

        (holder as NoticeViewHolder).bind(text, timestamp)
    }

}