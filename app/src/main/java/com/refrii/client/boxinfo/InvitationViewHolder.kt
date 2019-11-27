package com.refrii.client.boxinfo

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.refrii.client.R
import com.refrii.client.data.models.Invitation

class InvitationViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    @BindView(R.id.userTextView)
    lateinit var name: TextView
    @BindView(R.id.uninviteImageView)
    lateinit var uninviteImage: ImageView

    init {
        ButterKnife.bind(this, view)
    }

    fun bind(invitation: Invitation, listener: View.OnClickListener) {
        name.text = invitation.user?.name
        uninviteImage.setOnClickListener { listener.onClick(it.parent as View) }
    }
}