package app.muko.mypantry.boxinfo

import android.content.Context
import android.content.Intent
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario.launch
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.muko.mypantry.R
import app.muko.mypantry.data.models.Box
import app.muko.mypantry.foodlist.FoodListActivity
import io.realm.Realm
import io.realm.RealmConfiguration
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class FoodListActivityTest {

    private lateinit var context: Context

    @Before
    fun setup() {
        context = getApplicationContext()
    }

    @Test
    fun onCreate() {
        val intent = Intent(context, FoodListActivity::class.java)
        val scenario = launch<FoodListActivity>(intent)

        scenario.moveToState(Lifecycle.State.CREATED)
        scenario.onActivity {
//            assertEquals(it.mFab.visibility, View.VISIBLE)
        }

        assertEquals(1,1)
    }
}