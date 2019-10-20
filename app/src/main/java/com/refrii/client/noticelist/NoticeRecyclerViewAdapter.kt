package com.refrii.client.noticelist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.refrii.client.R
import com.refrii.client.data.models.Notice

class NoticeRecyclerViewAdapter(
        private var mNotices: List<Notice>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_notice, parent, false)

        return NoticeViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mNotices.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val notice = mNotices[position]

        (holder as NoticeViewHolder).bind(notice)
    }

    fun setNotices(notices: List<Notice>) {
        mNotices = notices

        notifyDataSetChanged()
    }
}