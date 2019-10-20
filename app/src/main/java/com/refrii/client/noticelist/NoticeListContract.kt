package com.refrii.client.noticelist

import com.refrii.client.data.models.Food
import com.refrii.client.data.models.Notice

interface NoticeListContract {

    interface View {
        fun setFood(food: Food)
        fun setNotices(notices: List<Notice>)
        fun resetForm()
        fun showToast(message: String)
    }

    interface Presenter {
        fun init(view: View)
        fun getFood(id: Int)
        fun createNotice(text: String)
    }
}