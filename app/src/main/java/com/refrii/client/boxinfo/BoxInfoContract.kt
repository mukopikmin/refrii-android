package com.refrii.client.boxinfo

import com.refrii.client.data.api.models.Box
import com.refrii.client.data.api.models.User

interface BoxInfoContract {

    interface View {
        fun setBox(box: Box?)
        fun showEditSharedUsersDialog(users: List<User>?)
        fun onLoading()
        fun onLoaded()
        fun showSnackbar(message: String?)
        fun showToast(message: String?)
        fun onDeleteCompleted(name: String?)
        fun removeBox(id: Int?, name: String?)
    }

    interface Presenter {
        fun takeView(view: View)
        fun getBox(id: Int)
        fun confirmRemovingBox()
        fun removeBox()
        fun updateBox()
        fun editSharedUsers()
        fun updateName(name: String)
        fun updateNotice(notice: String)
        fun updateSharedUsers(users: List<User>)
    }
}