package com.refrii.client.invitations

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.refrii.client.R
import com.refrii.client.data.models.Box
import com.refrii.client.data.models.Invitation
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class InvitationViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    @BindView(R.id.invitationRowContainer)
    lateinit var mInvitationRowContainer: View
    @BindView(R.id.nameTextView)
    lateinit var mNameText: TextView
    @BindView(R.id.emailTextView)
    lateinit var mEmailText: TextView
    @BindView(R.id.avatarImageView)
    lateinit var mAvatarImage: CircleImageView

    init {
        ButterKnife.bind(this, view)
    }

    fun bind(invitation: Invitation, box: Box, listener: View.OnLongClickListener) {
        val user = invitation.user

        mNameText.text = user?.name
        mEmailText.text = user?.email
        mInvitationRowContainer.setOnLongClickListener(listener)

        Picasso.get()
                .load(user?.avatarUrl)
                .placeholder(R.drawable.ic_outline_account_circle)
                .into(mAvatarImage)
    }
}