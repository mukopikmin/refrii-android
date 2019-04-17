package com.refrii.client

import com.nhaarman.mockitokotlin2.*
import com.refrii.client.boxinfo.BoxInfoContract
import com.refrii.client.boxinfo.BoxInfoPresenter
import com.refrii.client.data.api.models.Box
import com.refrii.client.data.api.models.Invitation
import com.refrii.client.data.api.source.ApiRepository
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import rx.Observable

class BoxInfoPresenterTest {

    @Rule
    @JvmField
    val mockito: MockitoRule = MockitoJUnit.rule()

    private val viewMock = mock<BoxInfoContract.View> {
        on { onLoading() } doAnswer {}
        on { onLoaded() } doAnswer {}
        on { setBox(any()) } doAnswer {}
        on { setSharedUsers(any()) } doAnswer {}
    }
    private val apiRepositoryMock = mock<ApiRepository> {
        on { getBoxFromCache(any()) } doReturn Observable.just(Box())
        on { getBox(any()) } doReturn Observable.just(Box())
        on { updateBox(any(), any(), any()) } doReturn Observable.just(Box())
        on { removeBox(any()) } doReturn Observable.empty()
        on { invite(any(), any()) } doReturn Observable.just(Invitation())
        on { uninvite(any(), any()) } doReturn Observable.empty()
    }

    private lateinit var presenter: BoxInfoPresenter

    @Before
    fun setUp() {
        presenter = BoxInfoPresenter(apiRepositoryMock)
        presenter.takeView(viewMock)
    }

    @Test
    fun takeView() {

    }

    @Test
    fun getBox_withCache() {
        presenter.getBox(any())

        verify(viewMock, times(1)).onLoading()
        verify(viewMock, times(1)).onLoaded()
        verify(viewMock, times(2)).setBox(any())
    }

    @Test
    fun updateBox() {
        val box = Box()

        box.id = 1
        box.name = "name"
        box.notice = "notice"

        presenter.setBox(box)
        presenter.updateBox()

        verify(viewMock, times(1)).onLoading()
        verify(viewMock, times(1)).onLoaded()
        verify(viewMock, times(2)).setBox(any())
        verify(viewMock, times(1)).showSnackbar(any())
    }

    @Test
    fun removeBox() {
        val box = Box()

        box.id = 1
        box.name = "name"
        box.notice = "notice"

        presenter.setBox(box)
        presenter.removeBox()

        verify(viewMock, times(1)).onLoading()
        verify(viewMock, times(1)).onLoaded()
//        verify(viewMock, times(1)).onDeleteCompleted(any())
    }

    @Test
    fun invite() {
        val box = Box()
        val email = "test@test.com"

        box.id = 1
        box.name = "name"
        box.notice = "notice"

        presenter.setBox(box)
        presenter.invite(email)

        verify(viewMock, times(1)).onLoading()
        verify(viewMock, times(1)).onLoaded()
        verify(viewMock, times(1)).showSnackbar(any())
//        verify(viewMock, times(1)).setSharedUsers(any())
    }

//    @Test
//    fun uninvite() {
//        val box = Box()
//        val user = User()
//
//        user.email = "test@test.com"
//        box.id = 1
//        box.name = "name"
//        box.notice = "notice"
//
//        presenter.setBox(box)
//        presenter.uninvite()
//
//        verify(viewMock, times(1)).onLoading()
//        verify(viewMock, times(1)).onLoaded()
//        verify(viewMock, times(1)).showSnackbar(any())
////        verify(viewMock, times(1)).setSharedUsers(any())
//    }

    @Test
    fun showInviteUserDialog() {
        presenter.showInviteUserDialog()

        verify(viewMock, times(1)).showInviteUserDialog(any())
    }
}