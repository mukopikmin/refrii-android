package app.muko.mypantry

import app.muko.mypantry.food.FoodActivity
import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test

import org.junit.Assert.*
import org.junit.runner.RunWith
import org.junit.runners.JUnit4


@RunWith(AndroidJUnit4::class)
//@Config(application = App::class)
class FoodActivityTest {
    private lateinit var activity: FoodActivity

//    @Before
//    fun setup() {
//        activity = Robolectric.buildActivity(FoodActivity::class.java)
//                .create()
//                .resume()
//                .get()
//    }
//
//    @After
//    fun teardown() {
//    }

    @Test
    fun hello_world_isCorrect() {
//        activity.mFab.performClick()
        assertEquals(1, 1);
    }
}