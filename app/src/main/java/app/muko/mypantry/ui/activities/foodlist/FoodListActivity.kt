package app.muko.mypantry.ui.activities.foodlist

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import app.muko.mypantry.R
import app.muko.mypantry.boxinfo.BoxInfoActivity
import app.muko.mypantry.data.models.Box
import app.muko.mypantry.data.models.Food
import app.muko.mypantry.data.models.User
import app.muko.mypantry.di.ViewModelFactory
import app.muko.mypantry.dialogs.ConfirmDialogFragment
import app.muko.mypantry.dialogs.CreateBoxDialogFragment
import app.muko.mypantry.settings.SettingsActivity
import app.muko.mypantry.ui.activities.shopplans.ShopPlansActivity
import app.muko.mypantry.ui.fragments.expiring.ExpiringFoodsFragment
import app.muko.mypantry.ui.fragments.foodlist.FoodListFragment
import app.muko.mypantry.ui.fragments.message.EmptyBoxMessageFragment
import app.muko.mypantry.ui.fragments.signin.SigninCallback
import app.muko.mypantry.ui.fragments.signin.SigninFragment
import app.muko.mypantry.ui.utils.CallbackNotification
import app.muko.mypantry.ui.utils.DrawerLocker
import app.muko.mypantry.unitlist.UnitListActivity
import butterknife.BindView
import butterknife.ButterKnife
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import dagger.android.HasAndroidInjector
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.app_bar_box.*
import javax.inject.Inject

class FoodListActivity : DaggerAppCompatActivity(),
        HasAndroidInjector,
        NavigationView.OnNavigationItemSelectedListener,
        DrawerLocker,
        SigninCallback,
        CallbackNotification {

    @BindView(R.id.contentBoxLayout)
    lateinit var viewContainer: View

    @BindView(R.id.toolbar)
    lateinit var mToolbar: Toolbar

    @BindView(R.id.drawer_layout)
    lateinit var mDrawer: DrawerLayout

    @BindView(R.id.nav_view)
    lateinit var mNavigationView: NavigationView

    @Inject
    lateinit var mPreference: SharedPreferences

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: FoodListViewModel

    override fun signinCompleted() {
        viewModel.reset()
        detectSigninStatus()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_box)
        ButterKnife.bind(this)
        setSupportActionBar(mToolbar)

        val toggle = ActionBarDrawerToggle(this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        mDrawer.addDrawerListener(ActionBarDrawerToggle(this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close))
        toggle.syncState()
        mNavigationView.setNavigationItemSelectedListener(this@FoodListActivity)

        viewModel = ViewModelProvider(this, viewModelFactory).get(FoodListViewModel::class.java)
    }

    override fun setDrawerLocked(shouldLock: Boolean) {
        if (shouldLock) {
            mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        } else {
            mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        }
    }

    fun setEmptyBoxMessage() {
        val boxId = viewModel.selectedBoxId.value ?: return
        val fragment = EmptyBoxMessageFragment.newInstance(boxId)
        val fragmentTransaction = supportFragmentManager.beginTransaction()

        fragmentTransaction.replace(R.id.testLinearLayout, fragment)
        fragmentTransaction.commit()
    }

    private fun detectSigninStatus() {
        val token = mPreference.getString(getString(R.string.preference_key_jwt), null)

        if (token == null) {
            val fragment = SigninFragment.newInstance()

            supportFragmentManager.beginTransaction()
                    .replace(R.id.testLinearLayout, fragment)
                    .commit()
        } else {
            viewModel.getBoxes()
        }
    }

    override fun onStart() {
        super.onStart()

        viewModel.boxes.observe(this, Observer {
            val selectedBoxId = viewModel.selectedBoxId.value

            setBoxes(it)
            restoreSelectedBoxState()

            if (selectedBoxId == null) {
                it?.firstOrNull()?.id?.let { boxId ->
                    viewModel.selectBox(boxId)
                }
            } else {
                if (viewModel.isBoxPicked(selectedBoxId).not()) {
                    viewModel.selectBox(it.first().id)
                }
            }
        })

        viewModel.selectedBoxId.observe(this, Observer { boxId ->
            val box = viewModel.boxes.value
                    ?.find { it.id == boxId }
                    ?: return@Observer

            setBox(box)
        })

        viewModel.user.observe(this, {
            setNavigationHeader(it)
            saveAccount(it)
        })

        viewModel.isSignedIn.observe(this, Observer {
            if (it) {
                viewModel.sync()
            } else {
                val fragment = SigninFragment.newInstance()

                supportFragmentManager.beginTransaction()
                        .replace(R.id.testLinearLayout, fragment)
                        .commit()
            }
        })

        viewModel.error.observe(this, Observer { showToast(it) })
        viewModel.notification.observe(this, Observer {
            if (it != null && it.isNotBlank()) {
                showSnackBar(it)
            }
        })

        viewModel.verifyAccount()
        detectSigninStatus()
    }

    override fun onPause() {
        super.onPause()

        storeSelectedBoxState()
    }

    override fun onStop() {
        super.onStop()

        viewModel.boxes.removeObservers(this)
        viewModel.foods.removeObservers(this)
        viewModel.user.removeObservers(this)
        viewModel.selectedBoxId.removeObservers(this)
        viewModel.isSignedIn.removeObservers(this)
        viewModel.error.removeObservers(this)
        viewModel.notification.removeObservers(this)
    }

    private fun restoreSelectedBoxState() {
        val boxId = mPreference.getInt(getString(R.string.preference_selected_box_id), -1)

        if (boxId != -1) {
            viewModel.selectBox(boxId)
        }
    }

    private fun storeSelectedBoxState() {
        val editor = mPreference.edit()

        viewModel.selectedBoxId.value?.let {
            editor.putInt(getString(R.string.preference_selected_box_id), it)
            editor.apply()
        }
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount == 1) {
            finish()
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.foodlist_menu, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_box_info -> showBoxInfo()
            R.id.action_box_sync -> viewModel.sync()
        }

        return super.onOptionsItemSelected(item)
    }

    private fun showBoxInfo() {
        val boxId = viewModel.selectedBoxId.value ?: return
        val box = viewModel.boxes.value?.find { it.id == boxId } ?: return
        val intent = Intent(this, BoxInfoActivity::class.java)

        intent.putExtra(getString(R.string.key_box_id), box.id)
        startActivityForResult(intent, REMOVE_BOX_REQUEST_CODE)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        val editor = mPreference.edit()

        editor.putInt(getString(R.string.preference_selected_box_id), id)
        editor.apply()

        if (!viewModel.isBoxPicked(id)) {
            when (id) {
                R.id.nav_expiring -> showExpiringFoods()
                R.id.nav_add_box -> addBox()
                R.id.nav_settings -> startActivity(Intent(this, SettingsActivity::class.java))
                R.id.nav_units -> startActivity(Intent(this, UnitListActivity::class.java))
                R.id.nav_shop_plans -> startActivity(Intent(this, ShopPlansActivity::class.java))
            }
        }

        mDrawer.closeDrawer(GravityCompat.START)

        return true
    }

    private fun showExpiringFoods() {
        val fragment = ExpiringFoodsFragment.newInstance()
        val fragmentTransaction = supportFragmentManager.beginTransaction()

        fragmentTransaction.replace(R.id.testLinearLayout, fragment)
        fragmentTransaction.commit()
    }

    private fun clearBoxes() {
        mNavigationView.menu
                .findItem(R.id.menu_boxes)
                .subMenu.clear()
        mNavigationView.menu
                .findItem(R.id.menu_invited_boxes)
                .subMenu.clear()
    }


    private fun setBoxes(boxes: List<Box>?) {
        boxes ?: return

        clearBoxes()

        boxes.filter { !it.isInvited }.forEach {
            setBoxesToNavigation(R.id.menu_boxes, it)
        }

        boxes.filter { it.isInvited }.forEach {
            setBoxesToNavigation(R.id.menu_invited_boxes, it)
        }

        if (boxes.isNotEmpty()) {
            val boxId = mPreference.getInt(getString(R.string.preference_selected_box_id), 0)
            val box = boxes.singleOrNull { it.id == boxId }

            if (box == null) {
//                mPresenter.selectBox(boxes.first())
            } else {
//                mPresenter.selectBox(box)
            }
        }
    }

    private fun setBoxesToNavigation(navId: Int, box: Box) {
        mNavigationView.menu
                .findItem(navId)
                .subMenu.add(Menu.NONE, box.id, Menu.NONE, box.name)
    }

    fun saveAccount(user: User?) {
        user ?: return

        val editor = mPreference.edit()

        editor.putInt(getString(R.string.preference_key_id), user.id)
        editor.apply()
    }

    fun setNavigationHeader(user: User?) {
        user ?: return

        val headerView = mNavigationView.getHeaderView(0)
        val nameTextView = headerView.findViewById<TextView>(R.id.nameNavHeaderTextView)
        val mailTextView = headerView.findViewById<TextView>(R.id.mailNavHeaderTextView)
        val avatarImageView = headerView.findViewById<ImageView>(R.id.lastUpdatedUserAvatarImageView)

        mNavigationView.setNavigationItemSelectedListener(this)

        nameTextView.text = user.name
        mailTextView.text = user.email

        Picasso.get().load(user.avatarUrl).into(avatarImageView)
    }

    fun setActionBar(title: String) {
        toolbar.title = title
    }

    fun setBox(box: Box) {
        val fragment = FoodListFragment.newInstance(box.id)

        supportFragmentManager.beginTransaction()
                .replace(R.id.testLinearLayout, fragment)
                .commit()
    }

    fun showConfirmDialog(food: Food?) {
        food ?: return

        val fragment = ConfirmDialogFragment.newInstance(food.name, "削除していいですか？", food.id)

        fragment.setTargetFragment(null, REMOVE_FOOD_REQUEST_CODE)
        fragment.show(supportFragmentManager, "delete_food")
    }

    fun showToast(message: String?) {
        message ?: return

        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun showSnackBar(message: String) {
        val snackBar = Snackbar.make(viewContainer, message, Snackbar.LENGTH_SHORT)

        snackBar.animationMode = Snackbar.ANIMATION_MODE_SLIDE
        snackBar.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK || data == null) return

        val fragments: List<Fragment>? = supportFragmentManager.fragments
        if (fragments != null) {
            for (fragment in fragments) {
                fragment.onActivityResult(requestCode, resultCode, data)
            }
        }

        when (requestCode) {
            ADD_FOOD_REQUEST_CODE -> onAddFoodCompleted()
            ADD_BOX_REQUEST_CODE -> onAddBoxCompleted()
            EDIT_FOOD_REQUEST_CODE -> onEditFoodCompleted(data)
            REMOVE_FOOD_REQUEST_CODE -> onRemoveFoodCompleted()
            REMOVE_BOX_REQUEST_CODE -> onRemoveBoxCompleted(data)
            CREATE_BOX_REQUEST_CODE -> createBox(data)
        }
    }

    private fun createBox(data: Intent?) {
        val name = data?.getStringExtra("name") ?: return

        viewModel.createBox(name)
    }

    private fun onAddFoodCompleted() {
        showSnackBar(getString(R.string.message_add_food_completed))
    }

    private fun onEditFoodCompleted(data: Intent?) {
        data?.let {
            val name = it.getStringExtra(getString(R.string.key_food_name))

            showSnackBar("$name が更新されました")
        }

//        mPresenter.getBoxes()
    }

    private fun onAddBoxCompleted() {
        showSnackBar(getString(R.string.message_add_box_completed))
    }

    private fun onRemoveFoodCompleted() {
//        mPresenter.removeFood()
    }

    private fun onRemoveBoxCompleted(data: Intent?) {
        val name = data?.getStringExtra("key_box_name") ?: return

        showSnackBar("$name が削除されました")
    }

    private fun addBox() {
        val fragment = CreateBoxDialogFragment.newInstance()

        fragment.setTargetFragment(null, CREATE_BOX_REQUEST_CODE)
        fragment.show(supportFragmentManager, "create_box")
    }

    companion object {
        private const val TAG = "FoodListActivity"
        private const val ADD_BOX_REQUEST_CODE = 101
        private const val ADD_FOOD_REQUEST_CODE = 102
        private const val EDIT_FOOD_REQUEST_CODE = 103
        private const val REMOVE_FOOD_REQUEST_CODE = 104
        private const val REMOVE_BOX_REQUEST_CODE = 105
        private const val CREATE_BOX_REQUEST_CODE = 106
        private const val SHOW_NOTICE_REQUEST_CODE = 107
    }
}
