package com.refrii.client.welcome

import com.refrii.client.data.models.Box
import com.refrii.client.data.source.ApiBoxRepository
import rx.Subscriber
import javax.inject.Inject

class WelcomePresenter
@Inject
constructor(private val mApiBoxRepository: ApiBoxRepository) : WelcomeContract.Presenter {

    private var mView: WelcomeContract.View? = null

    override fun takeView(view: WelcomeContract.View) {
        mView = view
    }

    override fun createBox(name: String) {
        mApiBoxRepository.createBox(name, null)
                .doOnSubscribe { mView?.onLoading() }
                .doOnUnsubscribe { mView?.onLoaded() }
                .subscribe(object : Subscriber<Box>() {
                    override fun onNext(t: Box?) {
                        t?.name?.let {
                            mView?.showToast("$it が作成されました。")
                        }
                    }

                    override fun onCompleted() {
                        mView?.onCreateBoxCompleted()
                    }

                    override fun onError(e: Throwable?) {
                        mView?.showToast(e?.message)
                    }
                })
    }
}