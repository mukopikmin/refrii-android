package com.refrii.client.dialogs.adapters

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.refrii.client.R

class NoticeViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    @BindView(R.id.textView)
    lateinit var textView: TextView
    @BindView(R.id.timestampTextView)
    lateinit var timestampTextView: TextView

    init {
        ButterKnife.bind(this, view)
    }

    fun bind(text: String, timestamp: String) {
        textView.text = text
        timestampTextView.text = timestamp
    }
}