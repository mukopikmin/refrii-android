package com.refrii.client.noticelist

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.refrii.client.R
import com.refrii.client.data.models.Notice
import java.text.SimpleDateFormat
import java.util.*

class NoticeViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    @BindView(R.id.textView)
    lateinit var textTextView: TextView
    @BindView(R.id.timestampTextView)
    lateinit var timestampTextView: TextView

    init {
        ButterKnife.bind(this, view)
    }

    fun bind(notice: Notice) {
        val context = textTextView.context
        val formatter = SimpleDateFormat(context.getString(R.string.date_format), Locale.getDefault())

        textTextView.text = notice.text
        timestampTextView.text = formatter.format(notice.createdAt)
    }
}