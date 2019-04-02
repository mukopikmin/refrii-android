package com.refrii.client.boxinfo

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.refrii.client.R

class SharedUserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val name: TextView = view.findViewById(R.id.userTextView)
    val uninviteImage: ImageView = view.findViewById(R.id.uninviteImageView)
}