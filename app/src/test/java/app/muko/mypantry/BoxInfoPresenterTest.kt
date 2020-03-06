package app.muko.mypantry

import app.muko.mypantry.boxinfo.BoxInfoContract
import app.muko.mypantry.boxinfo.BoxInfoPresenter
import app.muko.mypantry.data.models.Box
import app.muko.mypantry.data.models.Invitation
import app.muko.mypantry.data.models.User
import app.muko.mypantry.data.source.ApiBoxRepository
import app.muko.mypantry.helpers.MockitoHelper
import com.nhaarman.mockitokotlin2.*
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

    private val viewMock = mock<BoxInfoContract.View>()
    private val apiBoxRepositoryMock = mock<ApiBoxRepository> {
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
        presenter = BoxInfoPresenter(apiBoxRepositoryMock)
        presenter.takeView(viewMock)
    }

    @Test
    fun setBox() {
        val box = Box()

        presenter.setBox(box)

        verify(viewMock, times(1)).setBox(any())
    }

    @Test
    fun getBox() {
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
        verify(viewMock, times(1)).onDeleteCompleted(box.name)
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
        verify(viewMock, times(1)).setSharedUsers(MockitoHelper.any<List<User>>())
    }

    @Test
    fun uninvite() {
        val box = Box()
        val user = User()

        user.email = "test@test.com"
        box.id = 1
        box.name = "name"
        box.notice = "notice"

        presenter.setBox(box)
        presenter.setUser(user)
        presenter.uninvite()

        verify(viewMock, times(2)).onLoading()
        verify(viewMock, times(2)).onLoaded()
        verify(viewMock, times(1)).showSnackbar(any())
    }

    @Test
    fun showInviteUserDialog() {
        presenter.showInviteUserDialog()

        verify(viewMock, times(1)).showInviteUserDialog(MockitoHelper.any<List<User>>())
    }

    @Test
    fun updateName() {

    }

    @Test
    fun updateNotice() {

    }

    @Test
    fun confirmRemovingBox() {
        val box = Box()

        box.id = 1
        box.name = "name"

        presenter.setBox(box)
        presenter.confirmRemovingBox()

        verify(viewMock, times(1)).removeBox(box.id, box.name)
    }

    @Test
    fun confirmUninviting() {
        val box = Box()
        val user = User()

        box.name = "name"

        presenter.setBox(box)
        presenter.confirmUninviting(user)

        verify(viewMock, times(1)).uninvite(box.name, user)
    }
}