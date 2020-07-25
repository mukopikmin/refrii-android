package app.muko.mypantry.foodlist

import android.content.Context
import android.content.Intent
import android.view.Gravity
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.contrib.DrawerMatchers.isClosed
import androidx.test.espresso.contrib.DrawerMatchers.isOpen
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import app.muko.mypantry.App
import app.muko.mypantry.R
import app.muko.mypantry.di.DaggerTestAppComponent
import app.muko.mypantry.di.TestAppModule
import app.muko.mypantry.newfood.NewFoodActivity
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.MockitoAnnotations

@RunWith(AndroidJUnit4::class)
class FoodListActivityTest {

    private lateinit var context: Context

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)

        val app = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as App
        val testAppComponent = DaggerTestAppComponent.builder()
                .testAppModule(TestAppModule(app))
                .build()

        context = InstrumentationRegistry.getInstrumentation().targetContext
        app.appComponent = testAppComponent
        testAppComponent.inject(this)
    }

    @Test
    fun testInitialView() {
        val intent = Intent(context, FoodListActivity::class.java)
        val scenario = ActivityScenario.launch<FoodListActivity>(intent)

        scenario.moveToState(Lifecycle.State.CREATED)
        scenario.onActivity {
            assertEquals(it.mFab.visibility, View.VISIBLE)
        }
    }

    @Test
    fun testLaunchingNewFood() {
        val scenario = ActivityScenario.launch(FoodListActivity::class.java)

        Intents.init()

        scenario.moveToState(Lifecycle.State.STARTED)
        Thread.sleep(2000)
        scenario.onActivity {
            it.mFab.performClick()
        }
        Thread.sleep(2000)

        Intents.intended(hasComponent(NewFoodActivity::class.java.name))

        Intents.release()
    }

    @Test
    fun testLaunchingNewBox() {
        val scenario = ActivityScenario.launch(FoodListActivity::class.java)
//scenario.recreate()
        Intents.init()
//        scenario.recreate()
        scenario.moveToState(Lifecycle.State.RESUMED)
        Thread.sleep(2000)
//        scenario.onActivity {
        onView(withId(R.id.drawer_layout))
                .check(matches(isClosed(Gravity.LEFT))) // Left Drawer should be closed.
                .perform(DrawerActions.open())
                .check(matches(isOpen(Gravity.LEFT)))
        onData(withText("test box"))
                .check(matches(isDisplayed()))
                .perform(click())
//        }
        Intents.release()
    }
}

