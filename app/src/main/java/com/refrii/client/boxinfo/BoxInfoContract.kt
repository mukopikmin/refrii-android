package com.refrii.client.boxinfo

import com.refrii.client.data.models.Box
import com.refrii.client.data.models.Invitation
import com.refrii.client.data.models.User

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
        fun setInvitations(invitations: List<Invitation>)
        fun removeInvitation(boxName: String?, invitation: Invitation)
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
    }
}