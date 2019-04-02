package com.refrii.client.boxinfo

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.refrii.client.R
import com.refrii.client.data.api.models.User

class SharedUserRecyclerViewAdapter(private var mUsers: List<User>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mDeinviteClickListener: View.OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_shared_user, parent, false)

        return SharedUserViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mUsers.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
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