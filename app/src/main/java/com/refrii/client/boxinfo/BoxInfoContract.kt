package com.refrii.client.boxinfo

import com.refrii.client.data.models.Box
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
        fun setSharedUsers(users: List<User>?)
        fun uninvite(boxName: String?, user: User?)
    }

    interface Presenter {
        fun takeView(view: View)
        fun getBox(id: Int)
        fun invite(email: String)
        fun uninvite()
        fun showInviteUserDialog()
        fun confirmRemovingBox()
        fun removeBox()
        fun updateBox()
        fun updateName(name: String)
        fun updateNotice(notice: String)
        fun confirmUninviting(user: User?)
    }
}