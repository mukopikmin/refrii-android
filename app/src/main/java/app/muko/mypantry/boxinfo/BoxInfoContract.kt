package app.muko.mypantry.boxinfo

import app.muko.mypantry.data.models.Box
import app.muko.mypantry.data.models.Invitation
import app.muko.mypantry.data.models.User

interface BoxInfoContract {

    interface View {
        fun setBox(box: Box?)
        fun showInviteUserDialog(users: List<User>?)
        fun onLoading()
        fun onLoaded()
        fun showSnackbar(message: String?)
        fun showToast(message: String?)
        fun onDeleteCompleted(name: String?)
        fun removeBox(id: Int?, name: String?)
        fun setInvitations(invitations: List<Invitation>, box: Box)
        fun removeInvitation(boxName: String?, invitation: Invitation)
        fun showInvitations(box: Box)
    }

    interface Presenter {
        fun takeView(view: View)
        fun getBox(id: Int)
        fun createInvitation(email: String)
        fun removeInvitation()
        fun showInviteUserDialog()
        fun confirmRemovingBox()
        fun removeBox()
        fun updateBox()
        fun updateName(name: String)
        fun updateNotice(notice: String)
        fun confirmRemovingInvitation(invitation: Invitation)
        fun showInvitations()
    }
}