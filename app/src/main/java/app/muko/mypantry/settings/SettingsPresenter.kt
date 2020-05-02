package app.muko.mypantry.settings

import app.muko.mypantry.data.models.User
import app.muko.mypantry.data.source.ApiUserRepository
import io.reactivex.subscribers.DisposableSubscriber
import javax.inject.Inject

class SettingsPresenter
@Inject
constructor(private val mApiUserRepository: ApiUserRepository) : SettingsContract.Presenter {

    private var mView: SettingsContract.View? = null

    override fun takeView(view: SettingsContract.View) {
        mView = view
    }

    override fun updateUser(id: Int, name: String?) {
        mApiUserRepository.update(id, name)
                .subscribe(object : DisposableSubscriber<User>() {
                    override fun onNext(t: User?) {}

                    override fun onComplete() {}

                    override fun onError(e: Throwable?) {}
                })
    }

    override fun deleteLocalData() {
//        mApiUserRepository.deleteLocalData()
    }
}