package app.muko.mypantry

import app.muko.mypantry.data.models.Box
import app.muko.mypantry.data.models.Food
import app.muko.mypantry.data.models.Notice
import app.muko.mypantry.data.models.Unit
import app.muko.mypantry.data.source.ApiBoxRepository
import app.muko.mypantry.data.source.ApiFoodRepository
import app.muko.mypantry.data.source.ApiShopPlanRepository
import app.muko.mypantry.food.FoodContract
import app.muko.mypantry.food.FoodPresenter
import app.muko.mypantry.helpers.MockitoHelper
import com.nhaarman.mockitokotlin2.*
import io.realm.RealmList
import org.junit.Before
import org.junit.Test
import rx.Observable
import java.util.*
import kotlin.math.exp

class FoodPresenterTest {
//
//    lateinit var viewMock: FoodContract.View
//    lateinit var apiFoodRepositoryMock: ApiFoodRepository
//    lateinit var apiBoxRepositoryMock: ApiBoxRepository
//    lateinit var apiShopPlanRepositoryMock: ApiShopPlanRepository
//    lateinit var foodMock: Food
//    lateinit var presenter: FoodPresenter
//
//    @Before
//    fun setup() {
//        viewMock = mock()
//        apiFoodRepositoryMock = mock()
//        apiBoxRepositoryMock = mock()
//        apiShopPlanRepositoryMock = mock()
//        presenter = FoodPresenter(apiFoodRepositoryMock, apiBoxRepositoryMock, apiShopPlanRepositoryMock)
//        foodMock = Food()
//
//        presenter.takeView(viewMock)
//    }
//
//    @Test
//    fun getFood() {
//        val food = Food()
//        val unit = Unit()
//
//        food.id = 1
//        unit.id = 1
//        food.unit = unit
//
//        whenever(apiFoodRepositoryMock.getFoodFromCache(food.id)).then { Observable.just(food) }
//        whenever(apiFoodRepositoryMock.getFood(food.id)).then { Observable.just(food) }
//
//        presenter.getFood(food.id)
//
//        verify(viewMock, times(2)).setFood(food)
//    }
//
//    @Test
//    fun getUnits() {
//        val box = Box()
//        val unit = Unit()
//
//        box.id = 1
//
//        whenever(apiBoxRepositoryMock.getUnitsForBoxFromCache(box.id)).then { Observable.just(listOf(unit))}
//        whenever(apiBoxRepositoryMock.getUnitsForBox(box.id)).then { Observable.just(listOf(unit))}
//
//        presenter.getUnits(box.id)
//
//        verify(viewMock, times(2)).setUnits(any())
//    }
//
//    @Test
//    fun updateFood() {
//        val box = Box()
//        val food = Food()
//        val unit = Unit()
//        val name = "name"
//        val amount = 2.0
//        val expirationDate = Date()
//        val image = null
//
//        food.id = 1
//        food.box = box
//        food.unit = unit
//
//        whenever(apiFoodRepositoryMock.updateFood(food.id, name, amount, expirationDate, image, box.id, unit.id)).then {Observable.just(food)}
//
//        presenter.updateFood(food.id, name, amount, expirationDate, image, box.id, unit.id)
//
//        verify(viewMock, times(1)).showProgressBar()
//        verify(viewMock, times(1)).hideProgressBar()
//        verify(viewMock, times(1)).onUpdateCompleted(food)
//    }
//
//    @Test
//    fun getShopPlans() {
//
//    }
//
//    @Test
//    fun createShopPlan() {
//
//    }
//
//    @Test
//    fun completeShopPlan() {
//
//    }
}