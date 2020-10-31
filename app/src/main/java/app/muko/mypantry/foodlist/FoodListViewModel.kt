package app.muko.mypantry.foodlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import app.muko.mypantry.data.models.Box
import app.muko.mypantry.data.source.ApiBoxRepository
import app.muko.mypantry.data.source.ApiFoodRepository
import io.reactivex.subscribers.DisposableSubscriber
import javax.inject.Inject

class FoodListViewModel
@Inject
constructor(
        private val boxRepository: ApiBoxRepository,
        private val foodRepository: ApiFoodRepository
) : ViewModel() {

    val boxes: LiveData<List<Box>> = boxRepository.dao.getAllLiveData()
    val selectedBoxId: MutableLiveData<Int?> = MutableLiveData()
    val syncing: MutableLiveData<Boolean> = MutableLiveData(false)

    fun getBoxes() {
        boxRepository.getAll()
                .subscribe(object : DisposableSubscriber<List<Box>>() {
                    override fun onNext(t: List<Box>) {}
                    override fun onComplete() {}
                    override fun onError(e: Throwable?) {
//                        if (e is HttpException) {
//                            view.showToast(e.response()?.message())
//                        } else {
//                            view.showToast(e?.message)
//                        }
                    }
                })
    }

    fun sync() {
        boxRepository.getAll()
                .doOnSubscribe { syncing.value = true }
                .doFinally { syncing.value = false }
                .flatMap { foodRepository.getAll() }
                .subscribe()
    }

    fun isBoxPicked(boxId: Int): Boolean {
        this.selectedBoxId.value = boxId

        return boxes.value?.map { it.id }?.contains(boxId) ?: false
    }
}