package com.refrii.client.settings

interface SettingsContract {

    interface View

    interface Presenter {
        fun takeView(view: View)
        fun updateUser(id: Int, name: String?)
    }
}