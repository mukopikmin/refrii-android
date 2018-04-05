package com.refrii.client.boxinfo

import com.refrii.client.data.api.models.Box
import com.refrii.client.data.api.models.User

interface BoxInfoContract {

    interface View {
        fun setBox(box: Box?)
        fun showEditNameDialog(name: String?)
        fun showEditNoticeDialog(notice: String?)
        fun showEditSharedUsersDialog(users: List<User>?)
        fun onBeforeEdit()
        fun onEdited()
        fun onLoading()
        fun onLoaded()
        fun showSnackbar(message: String?)
        fun showToast(message: String?)
    }

    interface Presenter {
        fun takeView(view: View)
        fun getBox(id: Int)
        fun updateBox()
        fun editName()
        fun editNotice()
        fun editSharedUsers()
        fun updateName(name: String)
        fun updateNotice(notice: String)
        fun updateSharedUsers(users: List<User>)
    }
}