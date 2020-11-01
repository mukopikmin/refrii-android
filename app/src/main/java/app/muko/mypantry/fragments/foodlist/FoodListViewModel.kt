package app.muko.mypantry.fragments.foodlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import app.muko.mypantry.data.models.Box
import app.muko.mypantry.data.models.Food
import app.muko.mypantry.data.source.ApiBoxRepository
import app.muko.mypantry.data.source.ApiFoodRepository
import io.reactivex.subscribers.DisposableSubscriber
import retrofit2.HttpException
import javax.inject.Inject

class FoodListViewModel
@Inject
constructor(
        private val boxRepository: ApiBoxRepository,
        private val foodRepository: ApiFoodRepository
) : ViewModel() {

    lateinit var box: LiveData<Box>
    val foods = foodRepository.dao.getAllLiveData()
    var selectedFood = MutableLiveData<Food?>()

    fun initBox(boxId: Int) {
        box = boxRepository.dao.getLiveData(boxId)
    }

    fun getBox() {
        val id = this.box.value?.id ?: return

        this.boxRepository.get(id)
                .flatMap { foodRepository.getByBox(id) }
                .subscribe(object : DisposableSubscriber<Any>() {
                    override fun onNext(t: Any?) {
                    }

                    override fun onError(t: Throwable?) {
                        if (t is HttpException) {
                            unauthorizedHandler(t)
                        }
                    }

                    override fun onComplete() {
                    }
                })
    }

    fun getFoods() {
        foodRepository.getAll()
                .subscribe(object : DisposableSubscriber<Any>() {
                    override fun onNext(t: Any?) {
                    }

                    override fun onError(t: Throwable?) {
                        if (t is HttpException) {
                            unauthorizedHandler(t)
                        }
                    }

                    override fun onComplete() {
                    }
                })
    }

    private fun unauthorizedHandler(e: HttpException) {
        if (e.code() == 401) {
            print("aaaaaaaaaa")
        }
    }
}