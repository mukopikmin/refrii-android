package app.muko.mypantry.ui.fragments.foodaction

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import app.muko.mypantry.data.models.Food
import app.muko.mypantry.data.source.ApiFoodRepository
import io.reactivex.CompletableObserver
import io.reactivex.disposables.Disposable
import java.io.File
import javax.inject.Inject

class FoodActionDialogViewModel
@Inject
constructor(
        private val foodRepository: ApiFoodRepository
) : ViewModel() {

    lateinit var food: LiveData<Food>

    fun initFood(foodId: Int) {
        food = foodRepository.dao.getLiveData(foodId)

        getFood()
    }

    fun getFood() {
        val id = food.value?.id ?: return

        foodRepository.get(id).subscribe()
    }

    fun incrementFood() {
        food.value?.let {
            val step: Double = it.unit.step

            it.amount = it.amount + step
            updateFood(it)
        }
    }

    fun decrementFood() {
        food.value?.let {
            val step = it.unit.step
            val amount = it.amount - step

            if (amount < 0) {
                it.amount = 0.0
            } else {
                it.amount = amount
            }

            updateFood(it)
        }
    }

    private fun updateFood(food: Food, imageFile: File? = null) {
        foodRepository.update(food, imageFile)
                .subscribe(object : CompletableObserver {
                    override fun onComplete() {
                        getFood()
//                        view.showSnackbar("${food.name} の数量が更新されました")
                    }

                    override fun onSubscribe(d: Disposable) {}

                    override fun onError(e: Throwable) {
//                        view.showToast(e.message)
                    }
                })
    }

    fun removeFood() {
        val food = food.value ?: return

        foodRepository.remove(food)
                .subscribe(object : CompletableObserver {
                    override fun onSubscribe(d: Disposable) {
                        print("")
                    }

                    override fun onComplete() {
                        print("")
                    }

                    override fun onError(e: Throwable) {
                        print("")
                    }
                })
    }
}