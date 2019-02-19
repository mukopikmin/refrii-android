package com.refrii.client.foodlist

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.daimajia.swipe.util.Attributes
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.refrii.client.App
import com.refrii.client.R
import com.refrii.client.boxinfo.BoxInfoActivity
import com.refrii.client.data.api.models.Box
import com.refrii.client.data.api.models.Food
import com.refrii.client.dialogs.OptionsPickerDialogFragment
import com.refrii.client.food.FoodActivity
import com.refrii.client.newfood.NewFoodActivity
import com.refrii.client.settings.SettingsActivity
import com.refrii.client.signin.SigninActivity
import com.refrii.client.tasks.ImageDownloadTask
import com.refrii.client.tasks.ImageDownloadTaskCallback
import com.refrii.client.unitlist.UnitListActivity
import kotterknife.bindView
import java.util.*
import javax.inject.Inject

class FoodListActivity : AppCompatActivity(), FoodListContract.View, NavigationView.OnNavigationItemSelectedListener {

    private val mToolbar: Toolbar by bindView(R.id.toolbar)
    private val mFab: FloatingActionButton by bindView(R.id.fab)
    private val mDrawer: DrawerLayout by bindView(R.id.drawer_layout)
    private val mNavigationView: NavigationView by bindView(R.id.nav_view)
    private val mRecyclerView: RecyclerView by bindView(R.id.recyclerView)
    private val mProgressBar: ProgressBar by bindView(R.id.progressBar)

    private lateinit var mFirebaseAuth: FirebaseAuth

    @Inject
    lateinit var mPresenter: FoodListPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (application as App).getComponent().inject(this)

        setContentView(R.layout.activity_box)
        setSupportActionBar(mToolbar)

        hideProgressBar()

        val toggle = ActionBarDrawerToggle(this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        mDrawer.addDrawerListener(toggle)
        toggle.syncState()

        mRecyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        mRecyclerView.layoutManager = LinearLayoutManager(this)
        mFirebaseAuth = FirebaseAuth.getInstance()

        mFab.setOnClickListener { mPresenter.addFood() }
    }

    public override fun onStart() {
        super.onStart()

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val expiresAt = sharedPreferences.getLong("expirationTimestamp", 0) * 1000
        val currentUser = mFirebaseAuth.currentUser

        if (expiresAt < Date().time) {
            currentUser?.getIdToken(true)?.addOnCompleteListener {
                val editor = sharedPreferences.edit()

                editor.apply {
                    putString("jwt", it.result?.token)

                    it.result?.expirationTimestamp?.let {
                        putLong("expirationTimestamp", it)
                    }
                }
                editor.apply()

                mPresenter.takeView(this)
                mPresenter.getBoxes()
            }
        } else {
            mPresenter.takeView(this)
            mPresenter.getBoxes()
        }

        setNavigationHeader()
    }

    public override fun onResume() {
        super.onResume()

        val editor = PreferenceManager.getDefaultSharedPreferences(this@FoodListActivity).edit()

        editor.remove("selected_box_id")
        editor.apply()
    }

    public override fun onPause() {
        super.onPause()
    }

    public override fun onRestart() {
        super.onRestart()

        mPresenter.getBoxes()
    }

    override fun onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.box, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        when (id) {
            R.id.action_box_info -> {
                mPresenter.getBoxInfo()
            }
            R.id.action_box_sync -> {
                mPresenter.getBoxes()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun showBoxInfo(box: Box) {
        val intent = Intent(this, BoxInfoActivity::class.java)

        intent.putExtra("box_id", box.id)
        startActivity(intent)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (!mPresenter.pickBox(id)) {
            when (id) {
                R.id.nav_settings -> {
                    val intent = Intent(this, SettingsActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_signout -> signOut()
                R.id.nav_units -> {
                    val intent = Intent(this, UnitListActivity::class.java)
                    startActivity(intent)
                }
            }
        }

        mDrawer.closeDrawer(GravityCompat.START)

        return true
    }

    override fun setBoxes(boxes: List<Box>?) {
        boxes ?: return

        mNavigationView.setNavigationItemSelectedListener(this@FoodListActivity)
        val menu = mNavigationView.menu.findItem(R.id.menu_boxes)
        menu.subMenu.clear()

        boxes.forEach {
            menu.subMenu.add(Menu.NONE, it.id, Menu.NONE, it.name)
        }

        if (boxes.isNotEmpty()) {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this@FoodListActivity)
            val boxIndex = sharedPreferences.getInt("selected_box_index", 0)
            val box = boxes[boxIndex]

            mPresenter.selectBox(box)
        }
    }

    private fun setNavigationHeader() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val headerView = mNavigationView.getHeaderView(0)
        val nameTextView = headerView.findViewById<TextView>(R.id.nameNavHeaderTextView)
        val mailTextView = headerView.findViewById<TextView>(R.id.mailNavHeaderTextView)
        val avatarImageView = headerView.findViewById<ImageView>(R.id.avatarNavHeaderImageView)
        val name = sharedPreferences.getString("name", "name")
        val mail = sharedPreferences.getString("mail", "mail")
        val avatarUrl = sharedPreferences.getString("avatar", null)

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

        intent.putExtra("food_id", id)
        intent.putExtra("box_id", box?.id)

        startActivityForResult(intent, EDIT_FOOD_REQUEST_CODE)
    }

    override fun showOptionsDialog(food: Food?) {
        food ?: return

        val name = food.name ?: return
        val options = arrayOf("Show", "Remove", "Cancel")
        val fragment = OptionsPickerDialogFragment.newInstance(name, options, food.id)

        fragment.setTargetFragment(null, FOOD_OPTIONS_REQUEST_CODE)
        fragment.show(fragmentManager, "food_option")
    }

    override fun setFoods(box: Box?, foods: List<Food>?) {
        box ?: return
        foods ?: return

        val adapter = FoodRecyclerViewAdapter(foods)

        adapter.apply {
            mClickListener = View.OnClickListener {
                val position = mRecyclerView.getChildAdapterPosition(it)
                val food = adapter.getItemAtPosition(position)

                mPresenter.showFood(food.id)
            }

            mLongClickListener = View.OnLongClickListener {
                val position = mRecyclerView.getChildAdapterPosition(it)
                val food = adapter.getItemAtPosition(position)

                showOptionsDialog(food)

                true
            }

            mIncrementClickListener = View.OnClickListener {
                val position = mRecyclerView.getChildAdapterPosition(it)
                val food = adapter.getItemAtPosition(position)

                mPresenter.incrementFood(food)
            }

            mDecrementClickListener = View.OnClickListener {
                val position = mRecyclerView.getChildAdapterPosition(it)
                val food = adapter.getItemAtPosition(position)

                mPresenter.decrementFood(food)
            }

            mode = Attributes.Mode.Single
        }
        mRecyclerView.adapter = adapter
    }

    override fun onFoodUpdated() {
        mRecyclerView.adapter?.notifyDataSetChanged()
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
            ADD_FOOD_REQUEST_CODE -> {
                showSnackbar("Food added successfully")
                mPresenter.getBoxes()
            }
            EDIT_FOOD_REQUEST_CODE -> mPresenter.getBoxes()
            FOOD_OPTIONS_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    val option = data.getIntExtra("option", -1)

                    when(option) {
                    // Show
                        0 -> {
                            val foodId = data.getIntExtra("target_id", 0)
                            mPresenter.showFood(foodId)
                        }
                    // Remove
                        1 -> {
                            val foodId = data.getIntExtra("target_id", 0)
                            mPresenter.removeFood(foodId)
                        }
                    // Cancel or other
                        else -> return
                    }
                }
            }
        }
    }

    override fun addFood(box: Box?) {
        box ?: return

        val intent = Intent(this@FoodListActivity, NewFoodActivity::class.java)

        intent.putExtra("boxId", box.id)
        startActivityForResult(intent, ADD_FOOD_REQUEST_CODE)
    }

    override fun signOut() {
        val editor = PreferenceManager.getDefaultSharedPreferences(this).edit()
        val intent = Intent(this@FoodListActivity, SigninActivity::class.java)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()
        val googleSignInClient = GoogleSignIn.getClient(this, gso)

        googleSignInClient.revokeAccess()
//                .addOnCompleteListener(this) {
//                    Log.w("AAAAAAAAAAAA", "AAAAAAAAAAAA")
//                }

        editor.clear()
        editor.apply()

        startActivity(intent)
    }

    companion object {
        private const val ADD_FOOD_REQUEST_CODE = 101
        private const val FOOD_OPTIONS_REQUEST_CODE = 102
        private const val EDIT_FOOD_REQUEST_CODE = 103
    }
}
