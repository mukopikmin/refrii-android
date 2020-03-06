package app.muko.mypantry.invitations

import app.muko.mypantry.data.models.Box
import app.muko.mypantry.data.models.Invitation
import app.muko.mypantry.data.source.ApiBoxRepository
import app.muko.mypantry.data.source.ApiInvitationRepository
import rx.Subscriber
import javax.inject.Inject

class InvitationListPresenter
@Inject
constructor(
        private val mApiBoxRepository: ApiBoxRepository,
        private val mApiInvitationRepository: ApiInvitationRepository
) : InvitationListContract.Presenter {

    private var mView: InvitationListContract.View? = null
    private var mBox: Box? = null
    private var mInvitations: List<Invitation>? = null
    private var mInvitation: Invitation? = null

    override fun takeView(view: InvitationListContract.View) {
        mView = view
    }

    fun setBox(box: Box?) {
        mBox = box
        mInvitations = box?.invitations

        box?.invitations?.let { invitations ->
            mView?.setBox(box)
            mView?.setInvitations(invitations, box)
        }
    }

    override fun getBox(id: Int) {
        mApiBoxRepository.getBoxFromCache(id)
                .subscribe(object : Subscriber<Box>() {
                    override fun onNext(t: Box?) {
                        setBox(t)
                    }

                    override fun onCompleted() {}

                    override fun onError(e: Throwable?) {
                        e?.message?.let {
                            mView?.showToast(it)
                        }
                    }
                })

        mApiBoxRepository.getBox(id)
                .doOnSubscribe { mView?.onLoading() }
                .doOnUnsubscribe { mView?.onLoaded() }
                .subscribe(object : Subscriber<Box>() {
                    override fun onNext(t: Box?) {
                        setBox(t)
                    }

                    override fun onCompleted() {}

                    override fun onError(e: Throwable?) {
                        e?.message?.let {
                            mView?.showToast(it)
                        }
                    }
                })
    }

    override fun createInvitation(email: String) {
        mBox?.id?.let { id ->
            mApiBoxRepository.invite(id, email)
                    .doOnSubscribe { mView?.onLoading() }
                    .doOnUnsubscribe { mView?.onLoaded() }
                    .subscribe(object : Subscriber<Invitation>() {
                        override fun onNext(t: Invitation?) {
                            t?.let { mView?.onInvitationCreated(it) }
                        }

                        override fun onCompleted() {
                            getBox(id)
                        }

                        override fun onError(e: Throwable?) {
                            e?.message?.let {
                                mView?.showToast(it)
                            }
                        }
                    })
        }
    }

    override fun removeInvitation() {
        mInvitation?.id?.let { id ->
            mApiInvitationRepository.remove(id)
                    .doOnSubscribe { mView?.onLoading() }
                    .doOnUnsubscribe { mView?.onLoaded() }
                    .subscribe(object : Subscriber<Void>() {
                        override fun onNext(t: Void?) {}

                        override fun onCompleted() {
                            mView?.showSnackbar("共有を解除しました")
                            mBox?.id?.let {
                                getBox(it)
                            }
                        }

                        override fun onError(e: Throwable?) {
                            e?.message?.let {
                                mView?.showToast(it)
                            }
                        }
                    })
        }
    }

    override fun showOptionsDialog(invitation: Invitation) {
        mInvitation = invitation

        mView?.showOptionsDialog()
    }

    override fun confirmRemovingInvitation() {
        mInvitation?.let { invitation ->
            mBox?.name?.let {
                mView?.removeInvitation(it, invitation)
            }
        }
    }
}
