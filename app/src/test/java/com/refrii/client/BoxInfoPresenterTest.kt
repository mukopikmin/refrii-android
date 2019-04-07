package com.refrii.client

import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.refrii.client.boxinfo.BoxInfoContract
import com.refrii.client.boxinfo.BoxInfoPresenter
import com.refrii.client.data.api.models.Box
import com.refrii.client.data.api.models.User
import com.refrii.client.data.api.source.ApiRepository
import com.refrii.client.data.api.source.ApiRepositoryCallback
import com.refrii.client.di.DaggerAppComponent
import com.refrii.client.di.TestAppModule
import com.refrii.client.di.DaggerTestAppComponent
import com.refrii.client.foodlist.FoodListPresenter
import org.junit.Test

import org.junit.Assert.*
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.junit.Rule
import org.junit.Before
import org.mockito.*
import javax.inject.Inject

class BoxInfoPresenterTest {

    @Rule
    @JvmField
    val mockito = MockitoJUnit.rule()

//    @InjectMocks
//    lateinit var apiRepository: ApiRepository

    @Mock
    lateinit var view: BoxInfoContract.View

//    @Mock
//    lateinit var mBox: Box

    @Captor
    lateinit var callbackCaptor: ArgumentCaptor<ApiRepositoryCallback<Box>>

    @Inject
    lateinit var presenter: BoxInfoPresenter

    @Inject
    lateinit var apiRepository: ApiRepository

    @Before
    fun setUp() {
//        MockitoAnnotations.initMocks(this)
//        presenter = BoxInfoPresenter(apiRepository)
        presenter.takeView(view)
//        mBox = Box()

        val appComponent = DaggerTestComponent.builder()
                .appModule(TestAppModule())
                .build()
    }

    @Test
    @Throws(Exception::class)
    fun getBox_withCache() {
        val dummyBox = Box()

        presenter.getBox(1)

        verify(view, times(1)).setBox(dummyBox)
    }

//    @Test
//    @Throws(Exception::class)
//    fun showInvitationDialog() {
//        val users = emptyList<User>()
//
//        presenter.showInviteUserDialog()
//
//        verify(view, times(1)).showInviteUserDialog()
//    }
}