package app.muko.mypantry.boxinfo

import app.muko.mypantry.data.models.Box
import app.muko.mypantry.data.models.Invitation
import app.muko.mypantry.data.models.User
import app.muko.mypantry.data.source.ApiBoxRepository
import app.muko.mypantry.data.source.ApiInvitationRepository
import io.reactivex.subscribers.DisposableSubscriber
import javax.inject.Inject

class BoxInfoPresenter
@Inject
constructor(
        private val mApiBoxRepository: ApiBoxRepository,
        private val mApiInvitationRepository: ApiInvitationRepository
) : BoxInfoContract.Presenter {

    private var mView: BoxInfoContract.View? = null
    private var mBox: Box? = null
    private var mId: Int? = null
    private var mName: String? = null
    private var mNotice: String? = null
    private var mUser: User? = null
    private var mInvitations: List<Invitation>? = null
    private var mInvitation: Invitation? = null

    override fun takeView(view: BoxInfoContract.View) {
        mView = view
    }

    fun setBox(box: Box?) {
        mBox = box
        mId = box?.id
        mName = box?.name
        mNotice = box?.notice
        mInvitations = box?.invitations

        mView?.setBox(box)
        box?.invitations?.let { invitations ->
            mView?.setInvitations(invitations, box)
        }
    }

    fun setUser(user: User?) {
        mUser = user
    }

    override fun getBox(id: Int) {
        mApiBoxRepository.getBox(id)
                .doOnSubscribe { mView?.onLoading() }
                .doFinally { mView?.onLoaded() }
                .subscribe(object : DisposableSubscriber<Box>() {
                    override fun onNext(t: Box?) {
                        setBox(t)
                    }

                    override fun onComplete() {}

                    override fun onError(e: Throwable?) {
                        mView?.showToast(e?.message)
                    }
                })
    }

    override fun updateBox() {
        mId?.let { id ->
            mApiBoxRepository.updateBox(id, mName, mNotice)
                    .doOnSubscribe { mView?.onLoading() }
                    .doFinally { mView?.onLoaded() }
                    .subscribe(object : DisposableSubscriber<Box>() {
                        override fun onNext(t: Box?) {
                            mBox = t
                            mView?.setBox(t)
                            mView?.showSnackbar("カテゴリ ${t?.name} を更新しました")
                        }

                        override fun onComplete() {}

                        override fun onError(e: Throwable?) {
                            mView?.showToast(e?.message)
                        }
                    })
        }
    }

    override fun removeBox() {
        mId?.let { id ->
            mApiBoxRepository.removeBox(id)
                    .doOnSubscribe { mView?.onLoading() }
                    .doFinally { mView?.onLoaded() }
                    .subscribe(object : DisposableSubscriber<Void>() {
                        override fun onNext(t: Void?) {}

                        override fun onComplete() {
                            mView?.onDeleteCompleted(mName)
                        }

                        override fun onError(e: Throwable?) {
                            mView?.showToast(e?.message)
                        }
                    })
        }
    }

    override fun createInvitation(email: String) {
        mId?.let { id ->
            mApiBoxRepository.invite(id, email)
                    .doOnSubscribe { mView?.onLoading() }
                    .doFinally { mView?.onLoaded() }
                    .subscribe(object : DisposableSubscriber<Invitation>() {
                        override fun onNext(t: Invitation?) {
                            val name = t?.user?.name

                            mView?.showSnackbar("$name と共有しました")
                        }

                        override fun onComplete() {
                            mId?.let {
                                getBox(it)
                            }
                        }

                        override fun onError(e: Throwable?) {
                            mView?.showToast(e?.message)
                        }


                    })
        }
    }

    override fun removeInvitation() {
        mInvitation?.id?.let { id ->
            mApiInvitationRepository.remove(id)
                    .doOnSubscribe { mView?.onLoading() }
                    .doFinally { mView?.onLoaded() }
                    .subscribe(object : DisposableSubscriber<Void>() {
                        override fun onNext(t: Void?) {}

                        override fun onComplete() {
                            mView?.showSnackbar("共有を解除しました")
                            mId?.let {
                                getBox(it)
                            }
                        }

                        override fun onError(e: Throwable?) {
                            mView?.showToast(e?.message)
                        }


                    })
        }
    }

    override fun showInviteUserDialog() {
        mInvitations?.let { invitations ->
            mView?.showInviteUserDialog(invitations.map { it.user })
        }
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

    override fun confirmRemovingInvitation(invitation: Invitation) {
        mInvitation = invitation
        mView?.removeInvitation(mBox?.name, invitation)
    }

    override fun showInvitations() {
        mBox?.let {
            mView?.showInvitations(it)
        }
    }
}
