package app.muko.mypantry.settings

interface SettingsContract {

    interface View

    interface Presenter {
        fun takeView(view: View)
        fun updateUser(id: Int, name: String?)
        fun deleteLocalData()
    }
}