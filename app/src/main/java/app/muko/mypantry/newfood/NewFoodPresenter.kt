package app.muko.mypantry.newfood

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import app.muko.mypantry.data.models.Box
import app.muko.mypantry.data.models.Food
import app.muko.mypantry.data.models.Unit
import app.muko.mypantry.data.source.ApiBoxRepository
import app.muko.mypantry.data.source.ApiFoodRepository
import app.muko.mypantry.data.source.ApiUnitRepository
import io.reactivex.CompletableObserver
import io.reactivex.disposables.Disposable
import io.reactivex.subscribers.DisposableSubscriber
import java.util.*
import javax.inject.Inject

class NewFoodPresenter
@Inject
constructor(
        private val apiBoxRepository: ApiBoxRepository,
        private val apiFoodRepository: ApiFoodRepository,
        private val apiUnitRepository: ApiUnitRepository
) : NewFoodContract.Presenter {

    private lateinit var view: NewFoodContract.View
    private lateinit var boxLiveData: LiveData<Box>
    private lateinit var unitsLiveData: LiveData<List<Unit>>

    override fun init(view: NewFoodContract.View, boxId: Int) {
        this.view = view as NewFoodActivity
        boxLiveData = apiBoxRepository.dao.getLiveData(boxId)
        unitsLiveData = apiUnitRepository.dao.getAllLiveData()

        boxLiveData.observe(view, Observer {
            view.setBox(it)
            getUnits(it)
        })
        unitsLiveData.observe(view, Observer { units ->
            val box = boxLiveData.value ?: return@Observer
            val ownUnits = units.filter { it.user?.id == box.owner?.id }

            view.setUnits(ownUnits)
        })
    }

    override fun createFood(name: String, amount: Double, expirationDate: Date, unitLabel: String) {
        val box = boxLiveData.value ?: return
        val unit = unitsLiveData.value?.findLast { it.label == unitLabel } ?: return
        val food = Food.temp(name, amount, expirationDate, unit, box)

        apiFoodRepository.create(food)
                .doOnSubscribe { view.showProgressBar() }
                .doFinally { view.hideProgressBar() }
                .subscribe(object : CompletableObserver {
                    override fun onComplete() {
                        view.createCompleted(food)
                    }

                    override fun onSubscribe(d: Disposable) {}

                    override fun onError(e: Throwable) {
                        view.showToast(e.message)
                    }
                })
    }

    override fun getUnits(box: Box) {
        apiUnitRepository.getByBox(box)
                .subscribe(object : DisposableSubscriber<List<Unit>>() {
                    override fun onNext(t: List<Unit>?) {
                        view.setUnits(t)

                        if (t.isNullOrEmpty()) {
                            view.goToAddUnit()
                        }
                    }

                    override fun onComplete() {}

                    override fun onError(e: Throwable?) {
                        view.showToast(e?.message)
                    }
                })
    }

    override fun getBox(id: Int) {
        apiBoxRepository.get(id)
                .subscribe(object : DisposableSubscriber<Box>() {
                    override fun onNext(t: Box?) {
                        view.setBox(t)
                    }

                    override fun onComplete() {}

                    override fun onError(e: Throwable?) {
                        view.showToast(e?.message)
                    }
                })
    }

    override fun pickUnit(label: String): Unit? {
        return unitsLiveData.value?.firstOrNull { it.label == label }
    }
}