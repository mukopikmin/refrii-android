package com.refrii.client.boxinfo

import com.refrii.client.data.api.models.Box
import com.refrii.client.data.api.models.User
import com.refrii.client.data.api.source.ApiRepository
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
        mId = box?.id
        mName = box?.name
        mNotice = box?.notice
        mView?.setBox(box)
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
                    .subscribe({
                        mView?.onDeleteCompleted(mName)
                    }, {
                        mView?.showToast(it.message)
                    })
        }
    }

    override fun invite(email: String) {
        mId?.let { id ->
            mApiRepository.invite(id, email)
                    .doOnSubscribe { mView?.onLoading() }
                    .doOnUnsubscribe { mView?.onLoaded() }
                    .subscribe({
                        val name = it.user?.name

                        mView?.setSharedUsers(it.box?.invitedUsers)
                        mView?.showSnackbar("$name と共有しました")
                    }, {
                        mView?.showToast(it.message)
                    })
        }
    }

    override fun uninvite() {
        val email = mUser?.email ?: return

        mId?.let { id ->
            mApiRepository.uninvite(id, email)
                    .doOnSubscribe { mView?.onLoading() }
                    .doOnUnsubscribe { mView?.onLoaded() }
                    .subscribe({
                        mView?.showSnackbar("共有を解除しました")
                        getBox(id)
                    }, {
                        mView?.showToast(it.message)
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