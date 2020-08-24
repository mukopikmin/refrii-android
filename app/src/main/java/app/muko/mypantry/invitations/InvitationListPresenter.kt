package app.muko.mypantry.invitations

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import app.muko.mypantry.data.models.Box
import app.muko.mypantry.data.source.ApiBoxRepository
import app.muko.mypantry.data.source.ApiInvitationRepository
import io.reactivex.CompletableObserver
import io.reactivex.disposables.Disposable
import io.reactivex.subscribers.DisposableSubscriber
import javax.inject.Inject

class InvitationListPresenter
@Inject
constructor(
        private val apiBoxRepository: ApiBoxRepository,
        private val apiInvitationRepository: ApiInvitationRepository
) : InvitationListContract.Presenter {

    private lateinit var view: InvitationListContract.View
    private lateinit var boxLiveData: LiveData<Box>

    override fun init(view: InvitationListContract.View, boxId: Int) {
        this.view = view as InvitationListActivity
        boxLiveData = apiBoxRepository.dao.getLiveData(boxId)

        boxLiveData.observe(view, Observer {
            view.setBox(it)
            view.setInvitations(it.invitations)
        })
    }

    override fun getBox(id: Int) {
        apiBoxRepository.get(id)
                .doOnSubscribe { view.onLoading() }
                .doFinally { view.onLoaded() }
                .subscribe(object : DisposableSubscriber<Box>() {
                    override fun onNext(t: Box?) {}

                    override fun onComplete() {}

                    override fun onError(e: Throwable?) {
                        e?.message?.let {
                            view.showToast(it)
                        }
                    }
                })
    }

    override fun createInvitation(email: String) {
        val box = boxLiveData.value ?: return

        apiInvitationRepository.create(box, email)
                .doOnSubscribe { view.onLoading() }
                .doFinally { view.onLoaded() }
                .subscribe(object : CompletableObserver {
                    override fun onComplete() {
                        getBox(box.id)
                        view.onInvitationCreated(box)
                    }

                    override fun onSubscribe(d: Disposable) {}

                    override fun onError(e: Throwable) {
                        e.message?.let {
                            view.showToast(it)
                        }
                    }
                })
    }

    override fun removeInvitation(invitationId: Int) {
        val box = boxLiveData.value ?: return
        val invitation = box.invitations.findLast { it.id == invitationId } ?: return

        apiInvitationRepository.remove(invitation)
                .doOnSubscribe { view.onLoading() }
                .doFinally { view.onLoaded() }
                .subscribe(object : CompletableObserver {
                    override fun onComplete() {
                        view.showSnackbar("共有を解除しました")
                        getBox(box.id)
                    }

                    override fun onSubscribe(d: Disposable) {}

                    override fun onError(e: Throwable) {
                        e.message?.let {
                            view.showToast(it)
                        }
                    }
                })
    }

    override fun confirmRemovingInvitation(invitationId: Int) {
        val box = boxLiveData.value ?: return
        val invitation = box.invitations.findLast { it.id == invitationId } ?: return

        view.removeInvitation(invitation, box)
    }
}
