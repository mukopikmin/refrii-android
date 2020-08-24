package app.muko.mypantry.invitations

import app.muko.mypantry.data.models.Box
import app.muko.mypantry.data.models.Invitation

interface InvitationListContract {

    interface View {
        fun setBox(box: Box)
        fun onLoading()
        fun onLoaded()
        fun showSnackbar(message: String)
        fun showToast(message: String)
        fun setInvitations(invitations: List<Invitation>)
        fun removeInvitation(invitation: Invitation, box: Box)
        fun onInvitationCreated(box: Box)
    }

    interface Presenter {
        fun init(view: View, boxId: Int)
        fun getBox(id: Int)
        fun createInvitation(email: String)
        fun removeInvitation(invitationId: Int)
        fun confirmRemovingInvitation(invitationId: Int)
    }
}