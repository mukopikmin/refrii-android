package com.refrii.client.boxinfo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.refrii.client.R
import com.refrii.client.data.models.User

class SharedUserRecyclerViewAdapter(private var mUsers: List<User>) : androidx.recyclerview.widget.RecyclerView.Adapter<androidx.recyclerview.widget.RecyclerView.ViewHolder>() {

    private var mDeinviteClickListener: View.OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): androidx.recyclerview.widget.RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_shared_user, parent, false)

        return SharedUserViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mUsers.size
    }

    override fun onBindViewHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int) {
        val user = mUsers[position]

        (holder as SharedUserViewHolder).apply {
            name.text = user.name
            uninviteImage.setOnClickListener { mDeinviteClickListener?.onClick(it.parent as View) }
        }
    }

    fun setUsers(users: List<User>) {
        mUsers = users
        notifyDataSetChanged()
    }

    fun setDeinviteClickListener(listener: View.OnClickListener) {
        mDeinviteClickListener = listener
    }

    fun getItemAtPosition(position: Int): User {
        return mUsers[position]
    }
}