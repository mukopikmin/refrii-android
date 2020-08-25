package app.muko.mypantry.unitlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import app.muko.mypantry.data.models.Unit
import app.muko.mypantry.data.source.ApiUnitRepository
import io.reactivex.CompletableObserver
import io.reactivex.disposables.Disposable
import io.reactivex.subscribers.DisposableSubscriber
import javax.inject.Inject

class UnitListPresenter
@Inject
constructor(private val apiUnitRepository: ApiUnitRepository) : UnitListContract.Presenter {

    private lateinit var view: UnitListContract.View
    private lateinit var unitsLiveData: LiveData<List<Unit>>

    override fun init(view: UnitListContract.View) {
        this.view = view as UnitListActivity
        unitsLiveData = apiUnitRepository.dao.getAllLiveData()

        unitsLiveData.observe(view, Observer {
            view.setUnits(it)
        })
    }

    override fun getUnits(userId: Int) {
        apiUnitRepository.getAll()
                .doOnSubscribe { view.showProgressBar() }
                .doFinally { view.hideProgressBar() }
                .subscribe(object : DisposableSubscriber<List<Unit>>() {
                    override fun onNext(t: List<Unit>?) {
                        if (t.isNullOrEmpty()) {
                            view.showEmptyMessage()
                        } else {
                            view.hideEmptyMessage()
                        }
                    }

                    override fun onComplete() {}

                    override fun onError(e: Throwable?) {
                        view.showToast(e?.message)
                    }
                })
    }

    override fun removeUnit(id: Int) {
        val unit = unitsLiveData.value?.findLast { it.id == id } ?: return

        apiUnitRepository.remove(unit)
                .doOnSubscribe { view.showProgressBar() }
                .doFinally { view.hideProgressBar() }
                .subscribe(object : CompletableObserver {
                    override fun onComplete() {
                        view.showSnackbar("単位を削除しました")
                    }

                    override fun onSubscribe(d: Disposable) {}

                    override fun onError(e: Throwable) {
                        view.showToast(e.message)
                    }
                })
    }

    override fun getUnit(id: Int) {
        apiUnitRepository.get(id)
                .subscribe(object : DisposableSubscriber<Unit>() {
                    override fun onNext(t: Unit?) {
                        view.onUnitCreateCompleted(t)
                    }

                    override fun onComplete() {}

                    override fun onError(e: Throwable?) {
                        view.showToast(e?.message)
                    }
                })
    }
}