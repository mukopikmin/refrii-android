package com.refrii.client.settings

import com.refrii.client.data.models.User
import com.refrii.client.data.source.ApiUserRepository
import rx.Subscriber
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
                .subscribe(object : Subscriber<User>() {
                    override fun onNext(t: User?) {}

                    override fun onCompleted() {}

                    override fun onError(e: Throwable?) {}
                })
    }

    override fun deleteLocalData() {
        mApiUserRepository.deleteLocalData()
    }
}