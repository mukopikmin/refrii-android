package app.muko.mypantry.boxinfo

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import app.muko.mypantry.data.models.Box
import app.muko.mypantry.data.source.ApiBoxRepository
import io.reactivex.CompletableObserver
import io.reactivex.disposables.Disposable
import io.reactivex.subscribers.DisposableSubscriber
import javax.inject.Inject

class BoxInfoPresenter
@Inject
constructor(
        private val apiBoxRepository: ApiBoxRepository
) : BoxInfoContract.Presenter {

    private lateinit var view: BoxInfoContract.View
    private lateinit var boxLiveData: LiveData<Box>

    override fun init(view: BoxInfoContract.View, boxId: Int) {
        this.view = view as BoxInfoActivity
        this.boxLiveData = this.apiBoxRepository.dao.getLiveData(boxId)

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
                        view.showToast(e?.message)
                    }
                })
    }

    override fun updateBox(name: String, notice: String) {
        val box = boxLiveData.value ?: return

        box.name = name
        box.notice = notice

        apiBoxRepository.update(box)
                .doOnSubscribe { view.onLoading() }
                .doFinally { view.onLoaded() }
                .subscribe(object : CompletableObserver {
                    override fun onSubscribe(d: Disposable) {}

                    override fun onComplete() {
                        view.showSnackbar("カテゴリ ${box.name} を更新しました")
                    }

                    override fun onError(e: Throwable) {
                        view.showToast(e.message)
                    }
                })
    }

    override fun removeBox() {
        val box = boxLiveData.value ?: return

        apiBoxRepository.remove(box)
                .doOnSubscribe { view.onLoading() }
                .doFinally { view.onLoaded() }
                .subscribe(object : CompletableObserver {
                    override fun onComplete() {
                        view.onDeleteCompleted(box.name)
                    }

                    override fun onSubscribe(d: Disposable) {}

                    override fun onError(e: Throwable) {
                        view.showToast(e.message)
                    }
                })
    }

    override fun confirmRemovingBox() {
        val box = boxLiveData.value ?: return

        view.removeBox(box.id, box.name)
    }

    override fun showInvitations() {
        val box = boxLiveData.value ?: return

        view.showInvitations(box)
    }
}
