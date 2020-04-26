package app.muko.mypantry

import app.muko.mypantry.data.models.Box
import app.muko.mypantry.data.models.Food
import app.muko.mypantry.data.models.User
import app.muko.mypantry.data.source.ApiBoxRepository
import app.muko.mypantry.data.source.ApiFoodRepository
import app.muko.mypantry.data.source.ApiUserRepository
import app.muko.mypantry.foodlist.FoodListContract
import app.muko.mypantry.foodlist.FoodListPresenter
import app.muko.mypantry.helpers.MockitoHelper
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import rx.Observable

class FoodListPresenterTest {

//    @Rule
//    @JvmField
//    val mockito: MockitoRule = MockitoJUnit.rule()
//
//    private val viewMock = mock<FoodListContract.View>()
//    private val apiBoxRepositoryMock = mock<ApiBoxRepository> {
//        on { getFoodsInBoxFromCache(any()) } doReturn Observable.just(listOf())
//        on { getFoodsInBox(any()) } doReturn Observable.just(listOf())
//        on { getBoxesFromCache() } doReturn Observable.just(listOf())
//        on { getBoxes() } doReturn Observable.just(listOf())
//        on { createBox(any(), any()) } doReturn Observable.just(Box())
//    }
//    private val apiFoodRepositoryMock = mock<ApiFoodRepository> {
//        on { getExpiringFoodsFromCache() } doReturn Observable.just(listOf())
//        on { getExpiringFoods() } doReturn Observable.just(listOf())
//        on { updateFood(any(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull()) } doReturn Observable.just(Food())
//        on { removeFood(any()) } doReturn Observable.empty()
//    }
//    private val apiUserRepositoryMock = mock<ApiUserRepository> {
//        on { registerPushToken(any(), any()) } doReturn Observable.just(User())
//        on { deleteLocalData() } doAnswer {}
//    }
//
//    private lateinit var presenter: FoodListPresenter
//
//    @Before
//    fun setUp() {
//        presenter = FoodListPresenter(apiBoxRepositoryMock, apiFoodRepositoryMock, apiUserRepositoryMock)
//        presenter.takeView(viewMock)
//    }

//    @Test
//    fun setBoxes() {
//        val boxes = listOf(Box())
//
//        presenter.setBoxes(boxes)
//
//        verify(viewMock, times(1)).setBoxes(boxes)
//    }
//
//    @Test
//    fun setBox() {
//        val box = Box()
//        val foods = listOf<Food>()
//
//        presenter.setBox(box, foods)
//
//        verify(viewMock, times(1)).setFoods(MockitoHelper.any<String>(), MockitoHelper.any<List<Food>>())
//    }
//
//    @Test
//    fun getBox() {
//
//    }
//
//    @Test
//    fun getBoxInfo() {
//        val box = Box()
//
//        presenter.setBox(box)
//        presenter.getBoxInfo()
//
//        verify(viewMock, times(1)).showBoxInfo(any())
//    }
//
//    @Test
//    fun pickBox() {
//        val boxes = listOf(Box())
//
//        presenter.setBoxes(boxes)
//        presenter.pickBox(any())
//
//        verify(viewMock, times(2)).setFoods(MockitoHelper.any<String>(), MockitoHelper.any<List<Food>>())
//        verify(viewMock, times(1)).setEmptyMessage(MockitoHelper.any<List<Food>>())
//    }
//
//    @Test
//    fun getBoxes() {
//        presenter.getBoxes()
//
//        verify(viewMock, times(1)).clearBoxes()
//        verify(viewMock, times(2)).setBoxes(any())
//        verify(viewMock, times(1)).showToast(any())
//        verify(viewMock, times(1)).showProgressBar()
//        verify(viewMock, times(1)).hideProgressBar()
//    }
//
//    @Test
//    fun incrementFood() {
//        val food = Food()
//
//        presenter.selectFood(food)
//        presenter.incrementFood()
//
//        verify(viewMock, times(1)).onFoodUpdated(any())
//        verify(viewMock, times(1)).showSnackbar(any())
//        verify(viewMock, times(1)).showProgressBar()
//        verify(viewMock, times(1)).hideProgressBar()
//    }
//
//    @Test
//    fun decrementFood() {
//        val food = Food()
//
//        presenter.selectFood(food)
//        presenter.decrementFood()
//
//        verify(viewMock, times(1)).onFoodUpdated(any())
//        verify(viewMock, times(1)).showSnackbar(any())
//        verify(viewMock, times(1)).showProgressBar()
//        verify(viewMock, times(1)).hideProgressBar()
//    }
//
//    @Test
//    fun updateFood() {
//        val food = Food()
//        val amount = 1.0
//
//        food.id = 1
//
//        presenter.updateFood(food, amount)
//
//        verify(viewMock, times(1)).onFoodUpdated(any())
//        verify(viewMock, times(1)).showSnackbar(any())
//        verify(viewMock, times(1)).showProgressBar()
//        verify(viewMock, times(1)).hideProgressBar()
//    }
//
//    @Test
//    fun confirmRemovingFood() {
//        val food = Food()
//
//        presenter.selectFood(food)
//        presenter.confirmRemovingFood()
//
//        verify(viewMock, times(1)).showConfirmDialog(food)
//    }
//
//    @Test
//    fun removeFood() {
//        val food = Food()
//
//        presenter.selectFood(food)
//        presenter.removeFood()
//
//        verify(viewMock, times(1)).onFoodUpdated(any())
//        verify(viewMock, times(1)).showSnackbar(any())
//        verify(viewMock, times(2)).showProgressBar()
//        verify(viewMock, times(2)).hideProgressBar()
//        verify(viewMock, times(1)).clearBoxes()
//        verify(viewMock, times(2)).setBoxes(any())
//        verify(viewMock, times(1)).showToast(any())
//    }
//
//    @Test
//    fun createBox() {
//        val name = "name"
//        val notice = "notice"
//
//        presenter.createBox(name, notice)
//
//        verify(viewMock, times(1)).showSnackbar(any())
//        verify(viewMock, times(2)).showProgressBar()
//        verify(viewMock, times(2)).hideProgressBar()
//        verify(viewMock, times(1)).clearBoxes()
//        verify(viewMock, times(2)).setBoxes(any())
//        verify(viewMock, times(1)).showToast(any())
//    }
//
//    @Test
//    fun showFood() {
//        val box = Box()
//        val food = Food()
//
//        food.id = 1
//
//        presenter.setBox(box)
//        presenter.selectFood(food)
//        presenter.showFood()
//
//        verify(viewMock, times(1)).showFood(food.id, box)
//    }
//
//    @Test
//    fun selectBox() {
//        val box = Box()
//
//        presenter.selectBox(box)
//
//        verify(viewMock, times(2)).setFoods(MockitoHelper.any<String>(), MockitoHelper.any<List<Food>>())
//        verify(viewMock, times(1)).setEmptyMessage(any())
//    }
//
//    @Test
//    fun selectFood() {
//        val food = Food()
//
//        presenter.selectFood(food)
//
//        verify(viewMock, times(1)).showBottomNavigation(food)
//    }
//
//    @Test
//    fun addFood() {
//        presenter.addFood()
//
//        verify(viewMock, times(1)).addFood(MockitoHelper.any<Box>())
//    }
//
//    @Test
//    fun getExpiringFoods() {
//        presenter.getExpiringFoods()
//
//        verify(viewMock, times(2)).setFoods(MockitoHelper.any<String>(), MockitoHelper.any<List<Food>>())
//    }
//
//    @Test
//    fun registerPushToken() {
//        val userId = 1
//        val token = "token"
//
//        presenter.registerPushToken(userId, token)
//
//        verify(viewMock, times(1)).savePushToken(token)
//        verify(viewMock, times(1)).showToast(any())
//
//    }
//
//    @Test
//    fun deleteLocalData() {
//        presenter.deleteLocalData()
//
//        verify(viewMock, times(1)).clearBoxes()
//    }
}