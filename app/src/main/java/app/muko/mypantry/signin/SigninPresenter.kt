package app.muko.mypantry.signin

import app.muko.mypantry.data.models.User
import app.muko.mypantry.data.source.ApiUserRepository
import io.reactivex.subscribers.DisposableSubscriber
import retrofit2.HttpException
import javax.inject.Inject

class SigninPresenter
@Inject
constructor(private val mApiUserRepository: ApiUserRepository) : SigninContract.Presenter {

    private var mView: SigninContract.View? = null

    override fun takeView(view: SigninContract.View) {
        mView = view
    }

    override fun verifyAccount() {
        mApiUserRepository.verify()
                .subscribe(object : DisposableSubscriber<User>() {
                    override fun onNext(t: User?) {
                        mView?.saveAccount(t)
                    }

                    override fun onComplete() {
                        mView?.onLoginCompleted()
                    }

                    override fun onError(e: Throwable?) {
                        if ((e as HttpException).response()?.code() == 401) {
                            mView?.showToast("アカウントがありません。利用を始めるにはアカウントを作成してください。")
                        } else {
                            mView?.showToast(e.message)
                        }
                    }
                })
    }

    override fun signup() {
        mApiUserRepository.signup()
                .subscribe(object : DisposableSubscriber<User>() {
                    override fun onNext(t: User?) {
                        mView?.saveAccount(t)
                    }

                    override fun onComplete() {
                        mView?.onLoginCompleted()
                    }

                    override fun onError(e: Throwable?) {
                        mView?.showToast(e?.message)
                    }
                })
    }

    override fun deleteLocalData() {
//        mApiUserRepository.deleteLocalData()
    }
}