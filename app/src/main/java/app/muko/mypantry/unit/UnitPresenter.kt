package app.muko.mypantry.unit

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import app.muko.mypantry.data.models.Unit
import app.muko.mypantry.data.source.ApiUnitRepository
import io.reactivex.CompletableObserver
import io.reactivex.disposables.Disposable
import io.reactivex.subscribers.DisposableSubscriber
import javax.inject.Inject

class UnitPresenter
@Inject
constructor(
        private val apiUnitRepository: ApiUnitRepository
) : UnitContract.Presenter {

    private lateinit var view: UnitContract.View
    private lateinit var unitLiveData: LiveData<Unit>

    override fun init(view: UnitContract.View, id: Int) {
        this.view = view as UnitActivity
        unitLiveData = apiUnitRepository.dao.getLiveData(id)

        unitLiveData.observe(view, Observer {
            view.setUnit(it)
        })
    }

    override fun getUnit(id: Int) {
        apiUnitRepository.get(id)
                .doOnSubscribe { view.onLoading() }
                .doFinally { view.onLoaded() }
                .subscribe(object : DisposableSubscriber<Unit>() {
                    override fun onNext(t: Unit?) {}

                    override fun onComplete() {}

                    override fun onError(e: Throwable?) {
                        view.showToast(e?.message)
                    }
                })
    }

    override fun updateUnit(label: String, step: Double) {
        val unit = unitLiveData.value ?: return

        unit.label = label
        unit.step = step

        apiUnitRepository.update(unit)
                .doOnSubscribe { view.onLoading() }
                .doFinally { view.onLoaded() }
                .subscribe(object : CompletableObserver {
                    override fun onComplete() {
                        view.showSnackbar("${unit.label} を更新しました")
                    }

                    override fun onSubscribe(d: Disposable) {}

                    override fun onError(e: Throwable) {
                        view.showToast(e.message)
                    }
                })
    }
}