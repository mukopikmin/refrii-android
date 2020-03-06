package app.muko.mypantry.signin

import app.muko.mypantry.data.models.User

interface SigninContract {

    interface View {
        fun onLoading()
        fun onLoaded()
        fun onLoginCompleted()
        fun saveAccount(user: User?)
        fun showToast(message: String?)
    }

    interface Presenter {
        fun takeView(view: View)
        fun signup()
        fun verifyAccount()
    }
}