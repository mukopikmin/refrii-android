package app.muko.mypantry.ui.fragments.expiring

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import app.muko.mypantry.data.models.Food
import app.muko.mypantry.data.source.ApiFoodRepository
import javax.inject.Inject

class ExpiringFoodsViewModel
@Inject
constructor(
        private val foodRepository: ApiFoodRepository
) : ViewModel() {

    val foods: LiveData<List<Food>> = foodRepository.dao.getAllLiveData()
    var selectedFood: MutableLiveData<Food?> = MutableLiveData()

    fun getFoods() {
        foodRepository.getAll().subscribe()
    }
}