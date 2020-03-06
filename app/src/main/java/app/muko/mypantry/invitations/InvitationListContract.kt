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
        fun setInvitations(invitations: List<Invitation>, box: Box)
        fun removeInvitation(boxName: String, invitation: Invitation)
        fun onInvitationCreated(invitation: Invitation)
        fun showOptionsDialog()
    }

    interface Presenter {
        fun takeView(view: View)
        fun getBox(id: Int)
        fun createInvitation(email: String)
        fun removeInvitation()
        fun confirmRemovingInvitation()
        fun showOptionsDialog(invitation: Invitation)
    }
}