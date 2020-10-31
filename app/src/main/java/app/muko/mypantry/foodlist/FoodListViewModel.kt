package app.muko.mypantry.foodlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import app.muko.mypantry.data.models.Box
import app.muko.mypantry.data.models.User
import app.muko.mypantry.data.source.ApiBoxRepository
import app.muko.mypantry.data.source.ApiFoodRepository
import app.muko.mypantry.data.source.ApiUserRepository
import io.reactivex.subscribers.DisposableSubscriber
import javax.inject.Inject

class FoodListViewModel
@Inject
constructor(
        private val boxRepository: ApiBoxRepository,
        private val foodRepository: ApiFoodRepository,
        private val userRepository: ApiUserRepository
) : ViewModel() {

    val user: MutableLiveData<User?> = MutableLiveData(null)
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

    fun verifyAccount() {
        userRepository.verify()
                .subscribe(object : DisposableSubscriber<User>() {
                    override fun onComplete() {}

                    override fun onNext(t: User?) {
                        user.value = t
                    }

                    override fun onError(t: Throwable?) {}
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
        val isBox = boxes.value
                ?.map { it.id }
                ?.contains(boxId)
                ?: return false

        if (isBox) {
            this.selectedBoxId.value = boxId
        }

        return isBox
    }
}