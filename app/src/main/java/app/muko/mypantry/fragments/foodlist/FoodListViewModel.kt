package app.muko.mypantry.fragments.foodlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import app.muko.mypantry.data.models.Box
import app.muko.mypantry.data.models.Food
import app.muko.mypantry.data.source.ApiBoxRepository
import app.muko.mypantry.data.source.ApiFoodRepository
import javax.inject.Inject

class FoodListViewModel
@Inject
constructor(
        private val boxRepository: ApiBoxRepository,
        private val foodRepository: ApiFoodRepository
) : ViewModel() {

    lateinit var box: LiveData<Box>
    val foods: LiveData<List<Food>> = foodRepository.dao.getAllLiveData()
    var selectedFood: MutableLiveData<Food?> = MutableLiveData()

    fun initBox(boxId: Int) {
        box = boxRepository.dao.getLiveData(boxId)
    }

    fun getBox() {
        val id = this.box.value?.id ?: return

        this.boxRepository.get(id).subscribe()
    }

    fun getFoods() {
        val id = this.box.value?.id ?: return

        foodRepository.getByBox(id).subscribe()
    }

    fun selectFood(food: Food?) {
        val previous = this.selectedFood.value

        if (previous == null) {
            this.selectedFood.value = food
        } else {
            if (previous.id == food?.id) {
                this.selectedFood.value=null
            } else {
                this.selectedFood.value=food
            }
        }
    }
}