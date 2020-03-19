package app.muko.mypantry.invitations

import android.graphics.PorterDuff
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import app.muko.mypantry.R
import app.muko.mypantry.data.models.Box
import app.muko.mypantry.data.models.Invitation
import butterknife.BindView
import butterknife.ButterKnife
import com.ethanhua.skeleton.Skeleton
import com.squareup.picasso.Callback
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

        user?.let {
            if (user.avatarUrl.isNullOrEmpty()) {
                val context = mAvatarImage.context
                val avatar = context.getDrawable(R.drawable.ic_outline_account_circle)

                avatar?.let {
                    it.setTint(ContextCompat.getColor(context, android.R.color.darker_gray))
                    it.setTintMode(PorterDuff.Mode.SRC_IN)
                }

                mAvatarImage.setImageResource(R.drawable.ic_outline_account_circle)
            } else {
                val skeleton = Skeleton.bind(mAvatarImage)
                        .load(R.layout.skeleton_circle_image)
                        .duration(800)
                        .show()

                Picasso.get()
                        .load(user.avatarUrl)
                        .into(mAvatarImage, object : Callback {
                            override fun onSuccess() {
                                skeleton.hide()
                            }

                            override fun onError(e: Exception?) {}
                        })
            }
        }
    }
}