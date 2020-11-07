package app.muko.mypantry.foodlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import app.muko.mypantry.data.models.Box
import app.muko.mypantry.data.models.Food
import app.muko.mypantry.data.models.User
import app.muko.mypantry.data.source.ApiBoxRepository
import app.muko.mypantry.data.source.ApiFoodRepository
import app.muko.mypantry.data.source.ApiUserRepository
import io.reactivex.subscribers.DisposableSubscriber
import retrofit2.HttpException
import javax.inject.Inject

class FoodListViewModel
@Inject
constructor(
        private val boxRepository: ApiBoxRepository,
        private val foodRepository: ApiFoodRepository,
        private val userRepository: ApiUserRepository
) : ViewModel() {

    private val mutableUser = MutableLiveData<User?>(null)
    private val mutableSelectedBoxId = MutableLiveData<Int?>(null)
    private val mutableIsSignedIn = MutableLiveData<Boolean>(true)
    private val mutableError = MutableLiveData<String?>(null)
    private val mutableNotification = MutableLiveData<String?>(null)

    val boxes = boxRepository.dao.getAllLiveData()
    val foods = foodRepository.dao.getAllLiveData()
    val user: LiveData<User?> = mutableUser
    val selectedBoxId: LiveData<Int?> = mutableSelectedBoxId
    val isSignedIn: LiveData<Boolean> = mutableIsSignedIn
    val error: LiveData<String?> = mutableError
    val notification: LiveData<String?> = mutableNotification

    fun getBoxes() {
        boxRepository.getAll()
                .subscribe(object : DisposableSubscriber<List<Box>>() {
                    override fun onNext(t: List<Box>) {}
                    override fun onComplete() {}
                    override fun onError(t: Throwable?) {
                        notifyError(t)
                    }
                })
    }

    fun verifyAccount() {
        userRepository.verify()
                .subscribe(object : DisposableSubscriber<User>() {
                    override fun onComplete() {}

                    override fun onNext(t: User?) {
                        mutableUser.value = t
                    }

                    override fun onError(t: Throwable?) {
                        notifyError(t)
                    }
                })
    }

    fun sync() {
        boxRepository.getAll()
                .flatMap { foodRepository.getAll() }
                .subscribe(object : DisposableSubscriber<List<Food>>() {
                    override fun onNext(t: List<Food>?) {}

                    override fun onError(t: Throwable?) {
                        notifyError(t)

                        if (t is HttpException && t.code() == 401) {
                            mutableIsSignedIn.value = false
                        }
                    }

                    override fun onComplete() {}
                })
    }

    fun createBox(name: String) {
        val box = Box.temp(name, null)

        boxRepository.create(box)
                .andThen(boxRepository.getAll())
                .subscribe(object : DisposableSubscriber<List<Box>>() {
                    override fun onComplete() {
                        notify("カテゴリに $name が追加されました")
                    }

                    override fun onError(t: Throwable) {
                        notifyError(t)
                    }

                    override fun onNext(t: List<Box>?) {
                        t?.find { it.name == name }?.id?.let { boxId ->
                            isBoxPicked(boxId)
                        }
                    }
                })
    }

    private fun notifyError(e: Throwable?) {
        if (e is HttpException) {
            mutableError.value = e.response()?.message()
        } else {
            mutableError.value = e?.message
        }

        mutableError.value = null
    }

    private fun notify(message: String?) {
        mutableNotification.value = message
        mutableNotification.value = null
    }

    fun isBoxPicked(boxId: Int): Boolean {
        val isBox = boxes.value
                ?.map { it.id }
                ?.contains(boxId)
                ?: return false

        if (isBox) {
            selectBox(boxId)
        }

        return isBox
    }

    fun selectBox(boxId: Int) {
        if (selectedBoxId.value != boxId) {
            mutableSelectedBoxId.value = boxId
        }
    }
}