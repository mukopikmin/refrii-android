package app.muko.mypantry.boxinfo

import app.muko.mypantry.data.models.Box
import app.muko.mypantry.data.models.Invitation

interface BoxInfoContract {

    interface View {
        fun setBox(box: Box?)
        fun onLoading()
        fun onLoaded()
        fun showSnackbar(message: String?)
        fun showToast(message: String?)
        fun onDeleteCompleted(name: String?)
        fun removeBox(id: Int?, name: String?)
        fun setInvitations(invitations: List<Invitation>)
        fun showInvitations(box: Box)
    }

    interface Presenter {
        fun init(view: View, boxId: Int)
        fun getBox(id: Int)
        fun confirmRemovingBox()
        fun removeBox()
        fun updateBox(name: String, notice: String)
        fun showInvitations()
    }
}