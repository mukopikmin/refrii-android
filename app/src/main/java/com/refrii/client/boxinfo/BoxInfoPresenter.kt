package com.refrii.client.boxinfo

import com.refrii.client.data.models.Box
import com.refrii.client.data.models.Invitation
import com.refrii.client.data.models.User
import com.refrii.client.data.source.ApiRepository
import rx.Subscriber
import javax.inject.Inject

class BoxInfoPresenter
@Inject
constructor(private val mApiRepository: ApiRepository) : BoxInfoContract.Presenter {

    private var mView: BoxInfoContract.View? = null
    private var mBox: Box? = null
    private var mId: Int? = null
    private var mName: String? = null
    private var mNotice: String? = null
    private var mUser: User? = null

    override fun takeView(view: BoxInfoContract.View) {
        mView = view
    }

    fun setBox(box: Box?) {
        mBox = box
        mId = box?.id
        mName = box?.name
        mNotice = box?.notice

        mView?.setBox(box)
    }

    fun setUser(user: User?) {
        mUser = user
    }

    override fun getBox(id: Int) {
        mApiRepository.getBoxFromCache(id)
                .subscribe({
                    setBox(it)
                }, {
                    mView?.showToast(it.message)
                })

        mApiRepository.getBox(id)
                .doOnSubscribe { mView?.onLoading() }
                .doOnUnsubscribe { mView?.onLoaded() }
                .subscribe({
                    setBox(it)
                }, {
                    mView?.showToast(it.message)
                })
    }

    override fun updateBox() {
        mId?.let { id ->
            mApiRepository.updateBox(id, mName, mNotice)
                    .doOnSubscribe { mView?.onLoading() }
                    .doOnUnsubscribe { mView?.onLoaded() }
                    .subscribe({
                        mBox = it
                        mView?.setBox(it)
                        mView?.showSnackbar("Box ${it.name} is updated successfully")
                    }, {
                        mView?.showToast(it.message)
                    })
        }
    }

    override fun removeBox() {
        mId?.let { id ->
            mApiRepository.removeBox(id)
                    .doOnSubscribe { mView?.onLoading() }
                    .doOnUnsubscribe { mView?.onLoaded() }
                    .subscribe(object : Subscriber<Void>() {
                        override fun onNext(t: Void?) {}

                        override fun onCompleted() {
                            mView?.onDeleteCompleted(mName)
                        }

                        override fun onError(e: Throwable?) {
                            mView?.showToast(e?.message)
                        }
                    })
        }
    }

    override fun invite(email: String) {
        mId?.let { id ->
            mApiRepository.invite(id, email)
                    .doOnSubscribe { mView?.onLoading() }
                    .doOnUnsubscribe { mView?.onLoaded() }
                    .subscribe(object : Subscriber<Invitation>() {
                        override fun onNext(t: Invitation?) {
                            val name = t?.user?.name

                            mView?.setSharedUsers(t?.box?.invitedUsers)
                            mView?.showSnackbar("$name と共有しました")
                        }

                        override fun onCompleted() {}

                        override fun onError(e: Throwable?) {
                            mView?.showToast(e?.message)
                        }
                    })
        }
    }

    override fun uninvite() {
        val email = mUser?.email ?: return

        mId?.let { id ->
            mApiRepository.uninvite(id, email)
                    .doOnSubscribe { mView?.onLoading() }
                    .doOnUnsubscribe { mView?.onLoaded() }
                    .subscribe(object : Subscriber<Void>() {
                        override fun onNext(t: Void?) {}

                        override fun onCompleted() {
                            mView?.showSnackbar("共有を解除しました")
                            getBox(id)
                        }

                        override fun onError(e: Throwable?) {
                            mView?.showToast(e?.message)
                        }
                    })
        }
    }

    override fun showInviteUserDialog() {
        mView?.showInviteUserDialog(mBox?.invitedUsers)
    }

    override fun updateName(name: String) {
        mName = name
    }

    override fun updateNotice(notice: String) {
        mNotice = notice
    }

    override fun confirmRemovingBox() {
        mView?.removeBox(mId, mName)
    }

    override fun confirmUninviting(user: User?) {
        mUser = user
        mView?.uninvite(mBox?.name, user)
    }
}