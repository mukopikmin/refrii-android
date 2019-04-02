package com.refrii.client.boxinfo

import com.refrii.client.data.api.models.Box
import com.refrii.client.data.api.models.User

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
    }

    interface Presenter {
        fun takeView(view: View)
        fun getBox(id: Int)
        fun invite(email: String)
        fun uninvite(email: String)
        fun showInviteUserDialog()
        fun confirmRemovingBox()
        fun removeBox()
        fun updateBox()
        fun updateName(name: String)
        fun updateNotice(notice: String)
    }
}