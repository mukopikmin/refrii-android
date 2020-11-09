package app.muko.mypantry.ui.activities.food

import androidx.lifecycle.ViewModel
import app.muko.mypantry.data.source.ApiFoodRepository
import javax.inject.Inject

class FoodViewModel
@Inject
constructor(
        private val foodRepository: ApiFoodRepository
) : ViewModel() {


}