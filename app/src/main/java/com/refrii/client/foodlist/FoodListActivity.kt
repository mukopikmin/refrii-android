package com.refrii.client.foodlist

import android.app.Activity
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.GravityCompat
import butterknife.BindView
import butterknife.ButterKnife
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import com.refrii.client.App
import com.refrii.client.R
import com.refrii.client.SettingsActivity
import com.refrii.client.boxinfo.BoxInfoActivity
import com.refrii.client.data.models.Box
import com.refrii.client.data.models.Food
import com.refrii.client.dialogs.ConfirmDialogFragment
import com.refrii.client.dialogs.CreateBoxDialogFragment
import com.refrii.client.dialogs.NoticeDialogFragment
import com.refrii.client.food.FoodActivity
import com.refrii.client.newfood.NewFoodActivity
import com.refrii.client.shopplans.ShopPlansActivity
import com.refrii.client.signin.SignInActivity
import com.refrii.client.unitlist.UnitListActivity
import com.refrii.client.welcome.WelcomeActivity
import com.squareup.picasso.Picasso
import java.util.*
import javax.inject.Inject

class FoodListActivity : AppCompatActivity(), FoodListContract.View, NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.toolbar)
    lateinit var mToolbar: Toolbar
    @BindView(R.id.fab)
    lateinit var mFab: FloatingActionButton
    @BindView(R.id.drawer_layout)
    lateinit var mDrawer: androidx.drawerlayout.widget.DrawerLayout
    @BindView(R.id.nav_view)
    lateinit var mNavigationView: NavigationView
    @BindView(R.id.recyclerView)
    lateinit var mRecyclerView: androidx.recyclerview.widget.RecyclerView
    @BindView(R.id.progressBar)
    lateinit var mProgressBar: ProgressBar
    @BindView(R.id.emptyBoxMessageContainer)
    lateinit var mEmptyMessageContainer: View
    @BindView(R.id.addButton)
    lateinit var mAddFoodButton: AppCompatButton

    @BindView(R.id.coordinatorLayout)
    lateinit var mCoordinatorLayout: CoordinatorLayout
    @BindView(R.id.bottomNavigation)
    lateinit var mBottomNavigation: BottomNavigationView

    private lateinit var mFirebaseAuth: FirebaseAuth
    private lateinit var mPreference: SharedPreferences

    @Inject
    lateinit var mPresenter: FoodListPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (application as App).getComponent().inject(this)
        setContentView(R.layout.activity_box)
        ButterKnife.bind(this)

        setSupportActionBar(mToolbar)

        hideProgressBar()

        val toggle = ActionBarDrawerToggle(this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        mDrawer.addDrawerListener(toggle)
        toggle.syncState()
        mNavigationView.setNavigationItemSelectedListener(this@FoodListActivity)

        mRecyclerView.addItemDecoration(androidx.recyclerview.widget.DividerItemDecoration(this, androidx.recyclerview.widget.DividerItemDecoration.VERTICAL))
        mRecyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        mFirebaseAuth = FirebaseAuth.getInstance()
        mPreference = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        mEmptyMessageContainer.visibility = View.GONE

        mFab.setOnClickListener { mPresenter.addFood() }
        mAddFoodButton.setOnClickListener { mPresenter.addFood() }

        initPushNotification()

        mBottomNavigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_edit -> mPresenter.showFood()
                R.id.navigation_increment -> mPresenter.incrementFood()
                R.id.navigation_decrement -> mPresenter.decrementFood()
                R.id.navigation_notifications -> mPresenter.showNoticeDialog()
                R.id.navigation_delete -> mPresenter.confirmRemovingFood()
            }

            true
        }
    }

    override fun showNoticeDialog(name: String?, notice: String?) {
        name ?: return
        notice ?: return

        val fragment = NoticeDialogFragment.newInstance(name, notice)

        fragment.setTargetFragment(null, SHOW_NOTICE_REQUEST_CODE)
        fragment.show(supportFragmentManager, "show_notice")
    }

    private fun initPushNotification() {
        FirebaseMessaging.getInstance().subscribeToTopic(getString(R.string.notification_topic_push))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(
                    getString(R.string.notification_channel_id_push),
                    getString(R.string.notification_channel_name_push),
                    NotificationManager.IMPORTANCE_DEFAULT
            )

            channel.enableLights(true)
            channel.lightColor = Color.WHITE;
            channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC;
            manager.createNotificationChannel(channel);
        }

        val token = mPreference.getString(getString(R.string.preference_key_push_token), "")

        if (token == "") {
            FirebaseInstanceId.getInstance().instanceId
                    .addOnCompleteListener(OnCompleteListener { task ->
                        val userId = mPreference.getInt(application.getString(R.string.preference_key_id), 0)

                        if (!task.isSuccessful) {
                            return@OnCompleteListener
                        }

                        task.result?.token?.let {
                            mPresenter.registerPushToken(userId, it)
                        }
                    })
        }
    }

    override fun savePushToken(token: String) {
        val editor = mPreference.edit()

        editor.putString(application.getString(R.string.preference_key_push_token), token)
        editor.apply()
    }

    override fun onStart() {
        super.onStart()

        hideBottomNavigation()
    }

    override fun welcome() {
        val intent = Intent(this, WelcomeActivity::class.java)

        startActivity(intent)
    }

    private fun reauthorize() {
        val expiresAt = mPreference.getLong(getString(R.string.preference_key_expiration_timestamp), 0) * 1000
        val currentUser = mFirebaseAuth.currentUser

        if (expiresAt < Date().time) {
            if (currentUser == null) {
                signOut()
            } else {
                currentUser.getIdToken(true).addOnCompleteListener {
                    val editor = mPreference.edit()

                    editor.apply {
                        putString(getString(R.string.preference_key_jwt), it.result?.token)

                        it.result?.expirationTimestamp?.let {
                            putLong(getString(R.string.preference_key_expiration_timestamp), it)
                        }
                    }
                    editor.apply()

                    mPresenter.takeView(this)
                    mPresenter.getBoxes()
                    setNavigationHeader()
                }
            }
        } else {
            mPresenter.takeView(this)
            mPresenter.getBoxes()
            setNavigationHeader()
        }
    }

    override fun onPause() {
        super.onPause()

        storeSelectedBoxState()
    }

    private fun storeSelectedBoxState() {
        val editor = mPreference.edit()

        mPresenter.getBox()?.let {
            editor.putInt(getString(R.string.preference_selected_box_id), it.id)
            editor.apply()
        }
    }

    override fun onResume() {
        super.onResume()

        reauthorize()
    }

    override fun onBackPressed() {
        val adapter = mRecyclerView.adapter as FoodRecyclerViewAdapter

        when {
            mDrawer.isDrawerOpen(GravityCompat.START) -> mDrawer.closeDrawer(GravityCompat.START)
            adapter.isItemSelected() -> mPresenter.deselectFood()
            else -> super.onBackPressed()
        }
    }

    override fun deselectFood() {
        val adapter = mRecyclerView.adapter as FoodRecyclerViewAdapter

        adapter.deselectItem()
        hideBottomNavigation()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.foodlist_menu, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_box_info -> mPresenter.getBoxInfo()
            R.id.action_box_sync -> mPresenter.getBoxes()
        }

        return super.onOptionsItemSelected(item)
    }

    override fun showBoxInfo(box: Box) {
        val intent = Intent(this, BoxInfoActivity::class.java)

        intent.putExtra(getString(R.string.key_box_id), box.id)
        startActivityForResult(intent, REMOVE_BOX_REQUEST_CODE)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        val editor = mPreference.edit()

        editor.putInt(getString(R.string.preference_selected_box_id), id)
        editor.apply()

        hideBottomNavigation()

        if (!mPresenter.pickBox(id)) {
            when (id) {
                R.id.nav_expiring -> mPresenter.getExpiringFoods()
                R.id.nav_add_box -> addBox()
                R.id.nav_settings -> startActivity(Intent(this, SettingsActivity::class.java))
                R.id.nav_signout -> signOut()
                R.id.nav_units -> startActivity(Intent(this, UnitListActivity::class.java))
                R.id.nav_shop_plans -> startActivity(Intent(this, ShopPlansActivity::class.java))
            }
        }

        mDrawer.closeDrawer(GravityCompat.START)

        return true
    }

    override fun clearBoxes() {
        mNavigationView.menu
                .findItem(R.id.menu_boxes)
                .subMenu.clear()
        mNavigationView.menu
                .findItem(R.id.menu_invited_boxes)
                .subMenu.clear()
    }

    override fun setBoxes(boxes: List<Box>?) {
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
                mPresenter.selectBox(boxes.first())
            } else {
                mPresenter.selectBox(box)
            }
        }
    }

    private fun setBoxesToNavigation(navId: Int, box: Box) {
        mNavigationView.menu
                .findItem(navId)
                .subMenu.add(Menu.NONE, box.id, Menu.NONE, box.name)
    }

    private fun setNavigationHeader() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val headerView = mNavigationView.getHeaderView(0)
        val nameTextView = headerView.findViewById<TextView>(R.id.nameNavHeaderTextView)
        val mailTextView = headerView.findViewById<TextView>(R.id.mailNavHeaderTextView)
        val avatarImageView = headerView.findViewById<ImageView>(R.id.lastUpdatedUserAvatarImageView)
        val name = sharedPreferences.getString(getString(R.string.preference_key_name), getString(R.string.default_name))
        val mail = sharedPreferences.getString(getString(R.string.preference_key_mail), getString(R.string.default_mail))
        val avatarUrl = sharedPreferences.getString(getString(R.string.preference_key_avatar), null)

        mNavigationView.setNavigationItemSelectedListener(this)

        nameTextView.text = name
        mailTextView.text = mail

        Picasso.with(this).load(avatarUrl).into(avatarImageView)
    }

    override fun showFood(id: Int, box: Box?) {
        val intent = Intent(this@FoodListActivity, FoodActivity::class.java)

        intent.putExtra(getString(R.string.key_food_id), id)
        intent.putExtra(getString(R.string.key_box_id), box?.id)

        startActivityForResult(intent, EDIT_FOOD_REQUEST_CODE)
    }

    private fun updateFoods(boxName: String, foods: List<Food>) {
        val adapter = mRecyclerView.adapter as FoodRecyclerViewAdapter

        title = boxName
        adapter.setFoods(foods)
    }

    override fun setFoods(boxName: String?, foods: List<Food>?) {
        boxName ?: return
        foods ?: return

        val userId = mPreference.getInt(getString(R.string.preference_key_id), -1)

        if (mRecyclerView.adapter == null) {
            val adapter = FoodRecyclerViewAdapter(foods, userId)

            adapter.setOnClickListener(View.OnClickListener {
                val position = mRecyclerView.getChildAdapterPosition(it)
                val food = adapter.getItemAtPosition(position)

                if (mPresenter.isFoodSelected(food)) {
                    mPresenter.deselectFood()
                } else {
                    mPresenter.selectFood(food)
                }
            })

            mRecyclerView.adapter = adapter
            title = boxName
        } else {
            updateFoods(boxName, foods)
        }
    }

    override fun showConfirmDialog(food: Food?) {
        food ?: return

        val fragment = ConfirmDialogFragment.newInstance(food.name!!, "削除していいですか？", food.id)

        fragment.setTargetFragment(null, REMOVE_FOOD_REQUEST_CODE)
        fragment.show(supportFragmentManager, "delete_food")
    }

    override fun showBottomNavigation(food: Food) {
        mBottomNavigation.visibility = View.VISIBLE

        if (mRecyclerView.adapter != null) {
            val adapter = mRecyclerView.adapter as FoodRecyclerViewAdapter

            mRecyclerView.scrollToPosition(adapter.getItemPosition(food))
            adapter.selectItem(adapter.getItemPosition(food))
        }
    }

    override fun hideBottomNavigation() {
        mBottomNavigation.visibility = View.GONE

        if (mRecyclerView.adapter != null) {
            val adapter = mRecyclerView.adapter as FoodRecyclerViewAdapter

            adapter.deselectItem()
        }
    }

    override fun onFoodUpdated(food: Food?) {
        food ?: return

        val adapter = mRecyclerView.adapter as FoodRecyclerViewAdapter

        adapter.updateItem(food)
        mPresenter.selectFood(food)
    }

    override fun showProgressBar() {
        mProgressBar.visibility = View.VISIBLE
        mFab.hide()
    }

    override fun hideProgressBar() {
        mProgressBar.visibility = View.GONE
        mFab.show()
    }

    override fun showToast(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun showSnackbar(message: String?) {
        message ?: return

        Snackbar.make(mCoordinatorLayout, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_OK || data == null) return

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
        val notice = "" ?: return

        mPresenter.createBox(name, notice)
    }

    private fun onAddFoodCompleted() {
        showSnackbar(getString(R.string.message_add_food_completed))
        mPresenter.getBoxes()
    }

    private fun onEditFoodCompleted(data: Intent?) {
        data?.let {
            val name = it.getStringExtra(getString(R.string.key_food_name))

            showSnackbar("$name が更新されました")
        }

        mPresenter.getBoxes()
    }

    private fun onAddBoxCompleted() {
        showSnackbar(getString(R.string.message_add_box_completed))
    }

    private fun onRemoveFoodCompleted() {
        mPresenter.removeFood()
    }

    private fun onRemoveBoxCompleted(data: Intent?) {
        val name = data?.getStringExtra("key_box_name") ?: return

        showSnackbar("$name が削除されました")
    }

    private fun addBox() {
        val fragment = CreateBoxDialogFragment.newInstance()

        fragment.setTargetFragment(null, CREATE_BOX_REQUEST_CODE)
        fragment.show(supportFragmentManager, "create_box")
    }

    override fun addFood(box: Box?) {
        box ?: return

        val intent = Intent(this@FoodListActivity, NewFoodActivity::class.java)

        intent.putExtra(getString(R.string.key_box_id), box.id)
        startActivityForResult(intent, ADD_FOOD_REQUEST_CODE)
    }

    override fun signOut() {
        val editor = PreferenceManager.getDefaultSharedPreferences(applicationContext).edit()
        val intent = Intent(this@FoodListActivity, SignInActivity::class.java)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()
        val googleSignInClient = GoogleSignIn.getClient(this, gso)

        googleSignInClient.revokeAccess()
                .addOnCompleteListener(this) { Log.i(TAG, "Revoke access finished") }

        editor.clear()
        editor.apply()
        mPresenter.deleteLocalData()

        startActivity(intent)
    }

    override fun setEmptyMessage(foods: List<Food>?) {
        foods ?: return

        mEmptyMessageContainer.visibility = if (foods.isEmpty()) View.VISIBLE else View.GONE
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
