package com.refrii.client.boxinfo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.refrii.client.R
import com.refrii.client.data.models.Invitation

class InvitationsRecyclerViewAdapter(
        private var mInvitaions: List<Invitation>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mDeinviteClickListener: View.OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_shared_user, parent, false)

        return InvitationViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mInvitaions.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val invitation = mInvitaions[position]

        mDeinviteClickListener?.let {
            (holder as InvitationViewHolder).bind(invitation, it)
        }
    }

    fun setInvitations(invitations: List<Invitation>) {
        mInvitaions = invitations
        notifyDataSetChanged()
    }

    fun setDeinviteClickListener(listener: View.OnClickListener) {
        mDeinviteClickListener = listener
    }

    fun getItemAtPosition(position: Int): Invitation {
        return mInvitaions[position]
    }
}