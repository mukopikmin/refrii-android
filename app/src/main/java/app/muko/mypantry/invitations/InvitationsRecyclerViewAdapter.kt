package app.muko.mypantry.invitations

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.muko.mypantry.R
import app.muko.mypantry.data.models.Box
import app.muko.mypantry.data.models.Invitation

class InvitationsRecyclerViewAdapter(
        private var mInvitaions: List<Invitation>,
        private val mBox: Box
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    //    private var mDeinviteClickListener: View.OnClickListener? = null
    private var onLongClockListener: View.OnLongClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_shared_user, parent, false)

        return InvitationViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mInvitaions.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val invitation = mInvitaions[position]

        onLongClockListener?.let {
            (holder as InvitationViewHolder).bind(invitation, mBox, it)
        }
    }

    fun setInvitations(invitations: List<Invitation>) {
        mInvitaions = invitations
        notifyDataSetChanged()
    }

//    fun setDeinviteClickListener(listener: View.OnClickListener) {
//        mDeinviteClickListener = listener
//    }

    fun setOnLongClickListener(listener: View.OnLongClickListener) {
        onLongClockListener = listener
    }

    fun getItemAtPosition(position: Int): Invitation {
        return mInvitaions[position]
    }
}