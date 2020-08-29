package app.muko.mypantry.noticelist

import app.muko.mypantry.data.models.Food
import app.muko.mypantry.data.models.Notice

interface NoticeListContract {

    interface View {
        fun setFood(food: Food)
        fun setNotices(notices: List<Notice>)
        fun resetForm()
        fun onRemoveCompleted()
        fun showRemoveConfirmation(title: String, notice: Notice)
        fun showToast(message: String)
    }

    interface Presenter {
        fun init(view: View, foodId: Int)
        fun getFood(id: Int)
        fun createNotice(text: String)
        fun confirmRemovingNotice(notice: Notice)
        fun removeNotice()
    }
}