package app.muko.mypantry.ui.fragments.signin

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import app.muko.mypantry.data.models.User
import app.muko.mypantry.data.source.ApiUserRepository
import io.reactivex.subscribers.DisposableSubscriber
import retrofit2.HttpException
import javax.inject.Inject

class SigninViewModel
@Inject
constructor(
        private val userRepository: ApiUserRepository
) : ViewModel() {

    val user: MutableLiveData<User?> = MutableLiveData()
    val isAuthorizing: MutableLiveData<Boolean> = MutableLiveData(false)
    val errorMessage: MutableLiveData<String> = MutableLiveData()

    fun verifyAccount() {
        userRepository.verify()
                .doOnSubscribe { isAuthorizing.value = true }
                .doFinally { isAuthorizing.value = false }
                .subscribe(object : DisposableSubscriber<User>() {
                    override fun onNext(t: User?) {
                        user.value = t
                    }

                    override fun onComplete() {}

                    override fun onError(e: Throwable) {
                        if ((e as HttpException).response()?.code() == 401) {
                            errorMessage.value = "アカウントがありません。利用を始めるにはアカウントを作成してください。"
                        } else {
                            errorMessage.value = e.message()
                        }
                    }
                })
    }

    fun signup() {
        userRepository.signup()
                .doOnSubscribe { isAuthorizing.value = true }
                .doFinally { isAuthorizing.value = false }
                .subscribe(object : DisposableSubscriber<User>() {
                    override fun onNext(t: User?) {
                        user.value = t
                    }

                    override fun onComplete() {}

                    override fun onError(e: Throwable) {
                        errorMessage.value = e.message
                    }
                })
    }
}