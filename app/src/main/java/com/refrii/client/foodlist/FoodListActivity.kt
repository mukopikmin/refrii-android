package com.refrii.client.foodlist

import android.app.Activity
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.*
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import butterknife.BindView
import butterknife.ButterKnife
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import com.refrii.client.App
import com.refrii.client.R
import com.refrii.client.boxinfo.BoxInfoActivity
import com.refrii.client.data.api.models.Box
import com.refrii.client.data.api.models.Food
import com.refrii.client.dialogs.ConfirmDialogFragment
import com.refrii.client.dialogs.CreateBoxDialogFragment
import com.refrii.client.food.FoodActivity
import com.refrii.client.newfood.NewFoodActivity
import com.refrii.client.settings.SettingsActivity
import com.refrii.client.signin.SignInActivity
import com.refrii.client.tasks.ImageDownloadTask
import com.refrii.client.tasks.ImageDownloadTaskCallback
import com.refrii.client.unitlist.UnitListActivity
import java.util.*
import javax.inject.Inject

class FoodListActivity : AppCompatActivity(), FoodListContract.View, NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.toolbar)
    lateinit var mToolbar: Toolbar
    @BindView(R.id.fab)
    lateinit var mFab: FloatingActionButton
    @BindView(R.id.drawer_layout)
    lateinit var mDrawer: DrawerLayout
    @BindView(R.id.nav_view)
    lateinit var mNavigationView: NavigationView
    @BindView(R.id.recyclerView)
    lateinit var mRecyclerView: RecyclerView
    @BindView(R.id.progressBar)
    lateinit var mProgressBar: ProgressBar
    @BindView(R.id.emptyBoxMessageContainer)
    lateinit var mEmptyMessageContainer: View
    @BindView(R.id.addFoodButton)
    lateinit var mAddFoodButton: AppCompatButton

    @BindView(R.id.bottomMenu)
    lateinit var mBottomMenu: View
    @BindView(R.id.shadow)
    lateinit var mBottomNavigationShadow: View
    @BindView(R.id.incrementButton)
    lateinit var mIncrementButton: ImageView
    @BindView(R.id.decrementButton)
    lateinit var mDecrementButton: ImageView
    @BindView(R.id.amountTextView)
    lateinit var mAmountText: TextView
    @BindView(R.id.noticeTextView)
    lateinit var mNoticeText: TextView
    @BindView(R.id.editButton)
    lateinit var mEditButton: ImageView
    @BindView(R.id.deleteButton)
    lateinit var mDeleteButton: ImageView

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
        hideBottomNavigationWithoutAnimation()

        initPushNotification()

        val toggle = ActionBarDrawerToggle(this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        mDrawer.addDrawerListener(toggle)
        toggle.syncState()
        mNavigationView.setNavigationItemSelectedListener(this@FoodListActivity)

        mRecyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        mRecyclerView.layoutManager = LinearLayoutManager(this)
        mFirebaseAuth = FirebaseAuth.getInstance()
        mPreference = PreferenceManager.getDefaultSharedPreferences(this)

        mFab.setOnClickListener { mPresenter.addFood() }
        mIncrementButton.setOnClickListener { mPresenter.incrementFood() }
        mDecrementButton.setOnClickListener { mPresenter.decrementFood() }
        mEditButton.setOnClickListener { mPresenter.showFood() }
        mDeleteButton.setOnClickListener { mPresenter.confirmRemovingFood() }
        mAddFoodButton.setOnClickListener { mPresenter.addFood() }
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
    }

    override fun onStart() {
        super.onStart()

        reauthorize()
        hideBottomNavigationWithoutAnimation()
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

    override fun onRestart() {
        super.onRestart()

        mPresenter.getBoxes()
    }

    override fun onBackPressed() {
        val adapter = mRecyclerView.adapter as FoodRecyclerViewAdapter

        when {
            mDrawer.isDrawerOpen(GravityCompat.START) -> mDrawer.closeDrawer(GravityCompat.START)
            adapter.isItemSelected() -> deselectFood()
            else -> super.onBackPressed()
        }
    }

    private fun deselectFood() {
        val adapter = mRecyclerView.adapter as FoodRecyclerViewAdapter

        adapter.deselectItem()
        hideBottomNavigation()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.foodlist_menu, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        when (id) {
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
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val headerView = mNavigationView.getHeaderView(0)
        val nameTextView = headerView.findViewById<TextView>(R.id.nameNavHeaderTextView)
        val mailTextView = headerView.findViewById<TextView>(R.id.mailNavHeaderTextView)
        val avatarImageView = headerView.findViewById<ImageView>(R.id.avatarNavHeaderImageView)
        val name = sharedPreferences.getString(getString(R.string.preference_key_name), getString(R.string.default_name))
        val mail = sharedPreferences.getString(getString(R.string.preference_key_mail), getString(R.string.default_mail))
        val avatarUrl = sharedPreferences.getString(getString(R.string.preference_key_avatar), null)

        mNavigationView.setNavigationItemSelectedListener(this)

        nameTextView.text = name
        mailTextView.text = mail

        avatarUrl?.let {
            ImageDownloadTask(object : ImageDownloadTaskCallback {
                override fun onPostExecuted(result: Bitmap) {
                    avatarImageView.setImageBitmap(result)
                }
            }).execute(avatarUrl)
        }
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

        if (mRecyclerView.adapter == null) {
            val adapter = FoodRecyclerViewAdapter(foods)

            title = boxName
            adapter.setOnClickListener(View.OnClickListener {
                val position = mRecyclerView.getChildAdapterPosition(it)
                val food = adapter.getItemAtPosition(position)

                mPresenter.selectFood(food)
            })
            mRecyclerView.adapter = adapter
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
        mAmountText.text = "${food.amount} ${food.unit?.label}"
        mNoticeText.text = food.notice
        mBottomNavigationShadow.visibility = View.VISIBLE

        mBottomMenu.animate()
                .translationY(0.toFloat())
                .withStartAction { mBottomMenu.visibility = View.VISIBLE }
                .duration = 200
        mFab.hide()

        if (mRecyclerView.adapter != null) {
            val adapter = mRecyclerView.adapter as FoodRecyclerViewAdapter

            mRecyclerView.scrollToPosition(adapter.getItemPosition(food))
            adapter.selectItem(adapter.getItemPosition(food))
        }
    }

    private fun hideBottomNavigationWithoutAnimation() {
        mBottomMenu.visibility = View.GONE
        mBottomNavigationShadow.visibility = View.GONE
        mFab.show()

        if (mRecyclerView.adapter != null) {
            val adapter = mRecyclerView.adapter as FoodRecyclerViewAdapter

            adapter.deselectItem()
        }
    }

    override fun hideBottomNavigation() {
        mBottomNavigationShadow.visibility = View.GONE
        mBottomMenu.animate()
                .translationY(mBottomMenu.height.toFloat())
                .withEndAction { mBottomMenu.visibility = View.GONE }
                .duration = 200
        mFab.show()

        if (mRecyclerView.adapter != null) {
            val adapter = mRecyclerView.adapter as FoodRecyclerViewAdapter

            adapter.deselectItem()
        }
    }

    override fun onFoodUpdated(food: Food?) {
        mRecyclerView.adapter?.notifyDataSetChanged()

        food?.let {
            showBottomNavigation(food)
        }
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

        Snackbar.make(mRecyclerView, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_OK || data == null) return

        when (requestCode) {
            ADD_FOOD_REQUEST_CODE -> onAddFoodCompleted()
            ADD_BOX_REQUEST_CODE -> onAddBoxCompleted()
            EDIT_FOOD_REQUEST_CODE -> mPresenter.getBoxes()
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
        val editor = PreferenceManager.getDefaultSharedPreferences(this).edit()
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

        if (foods.isEmpty()) {
            mEmptyMessageContainer.visibility = View.VISIBLE
        } else {
            mEmptyMessageContainer.visibility = View.GONE
        }
    }

    companion object {
        private const val TAG = "FoodListActivity"
        private const val ADD_BOX_REQUEST_CODE = 101
        private const val ADD_FOOD_REQUEST_CODE = 102
        private const val EDIT_FOOD_REQUEST_CODE = 103
        private const val REMOVE_FOOD_REQUEST_CODE = 104
        private const val REMOVE_BOX_REQUEST_CODE = 105
        private const val CREATE_BOX_REQUEST_CODE = 106
    }
}
