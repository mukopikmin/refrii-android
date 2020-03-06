package app.muko.mypantry

import app.muko.mypantry.data.models.Food
import app.muko.mypantry.data.models.Unit
import app.muko.mypantry.data.source.ApiBoxRepository
import app.muko.mypantry.data.source.ApiFoodRepository
import app.muko.mypantry.data.source.ApiShopPlanRepository
import app.muko.mypantry.food.FoodContract
import app.muko.mypantry.food.FoodPresenter
import app.muko.mypantry.helpers.MockitoHelper
import com.nhaarman.mockitokotlin2.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import rx.Observable
import java.util.*

class FoodPresenterTest {

    @Rule
    @JvmField
    val mockito: MockitoRule = MockitoJUnit.rule()

    private val viewMock = mock<FoodContract.View>()
    private val apiBoxRepositoryMock = mock<ApiBoxRepository> {
        on { getUnitsForBox(any()) } doReturn Observable.just(listOf())
        on { getUnitsForBoxFromCache(any()) } doReturn Observable.just(listOf())
    }
    private val apiFoodRepositoryMock = mock<ApiFoodRepository> {
        on { getFood(any()) } doReturn Observable.just(Food())
        on { getFoodFromCache(any()) } doReturn Observable.just(Food())
        on { updateFood(any(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull()) } doReturn Observable.just(Food())
        on { getShopPlansForFood(any()) } doReturn Observable.just(listOf())
        on { getShopPlansForFoodFromCache(any()) } doReturn Observable.just(listOf())
    }
    private val apiShopPlanRepositoryMock = mock<ApiShopPlanRepository> { }

    private lateinit var presenter: FoodPresenter

    @Before
    fun setUp() {
        presenter = FoodPresenter(apiFoodRepositoryMock, apiBoxRepositoryMock, apiShopPlanRepositoryMock)
        presenter.takeView(viewMock)
    }

    @Test
    fun setFood() {
        presenter.setFood(any())

        verify(viewMock, times(1)).setFood(MockitoHelper.any<Food>())
        verify(viewMock, times(1)).setSelectedUnit(MockitoHelper.any<Int>())
    }

    @Test
    fun setUnits() {
        presenter.setUnits(any())

        verify(viewMock, times(1)).setUnits(MockitoHelper.any<List<Unit>>())
        verify(viewMock, times(1)).setSelectedUnit(MockitoHelper.any<Int>())
    }

    @Test
    fun getFood() {
        presenter.getFood(any())

        verify(viewMock, times(2)).setFood(any())
        verify(viewMock, times(2)).setSelectedUnit(MockitoHelper.any<Int>())
    }

    @Test
    fun getUnits() {
        presenter.getUnits(any())

        verify(viewMock, times(2)).setUnits(any())
        verify(viewMock, times(2)).setSelectedUnit(MockitoHelper.any<Int>())
    }

    @Test
    fun updateFood() {
        val food = Food()

        food.id = 1

        presenter.setFood(food)
        presenter.updateFood()

        verify(viewMock, times(1)).showProgressBar()
        verify(viewMock, times(1)).hideProgressBar()
        verify(viewMock, times(1)).onUpdateCompleted(MockitoHelper.any<Food>())
    }

    @Test
    fun selectUnit() {
        presenter.selectUnit(any())

        verify(viewMock, times(1)).setSelectedUnit(any())
    }

    @Test
    fun editExpirationDate() {
        presenter.editExpirationDate()

        verify(viewMock, times(1)).showEditDateDialog(MockitoHelper.any<Date>())
    }

    @Test
    fun updateName() {

    }

    @Test
    fun updateAmount() {

    }

    @Test
    fun updateNotice() {

    }

    @Test
    fun updateExpirationDate() {
        val date = Date()

        presenter.updateExpirationDate(date)

        verify(viewMock, times(1)).setExpirationDate(date)
    }
}