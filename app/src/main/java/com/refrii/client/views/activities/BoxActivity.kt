package com.refrii.client.views.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.util.Log
import android.view.View
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.widget.*

import com.daimajia.swipe.util.Attributes
import com.refrii.client.*
import com.refrii.client.factories.RetrofitFactory
import com.refrii.client.models.Box
import com.refrii.client.models.Food
import com.refrii.client.services.BoxService
import com.refrii.client.services.FoodService
import com.refrii.client.tasks.ImageDownloadTask
import com.refrii.client.views.adapters.FoodListAdapter
import com.refrii.client.views.fragments.OptionsPickerDialogFragment
import io.realm.Realm
import io.realm.RealmConfiguration
import kotterknife.bindView

import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class BoxActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val mToolbar: Toolbar by bindView(R.id.toolbar)
    private val mFab: FloatingActionButton by bindView(R.id.fab)
    private val mDrawer: DrawerLayout by bindView(R.id.drawer_layout)
    private val mNavigationView: NavigationView by bindView(R.id.nav_view)
    private val mListView: ListView by bindView(R.id.listView)
    private val mProgressBar: ProgressBar by bindView(R.id.progressBar)

    private var mBoxes: List<Box>? = null
    private var mBox: Box? = null
    private var mRealm: Realm? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_box)
        setSupportActionBar(mToolbar)

        val toggle = ActionBarDrawerToggle(this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        mDrawer.addDrawerListener(toggle)
        toggle.syncState()

        Realm.setDefaultConfiguration(RealmConfiguration.Builder(this).build())
        mRealm = Realm.getDefaultInstance()

        mFab.setOnClickListener {
            val intent = Intent(this@BoxActivity, NewFoodActivity::class.java)
            intent.putExtra("boxId", mBox!!.id)
            startActivityForResult(intent, ADD_FOOD_REQUEST_CODE)
        }
    }

    public override fun onStart() {
        super.onStart()

        setNavigationHeader()
    }

    public override fun onPause() {
        super.onPause()

        mBox?.let {
            val editor = PreferenceManager.getDefaultSharedPreferences(this@BoxActivity).edit()
            editor.putInt("selected_box_index", mBoxes!!.indexOf(it))
            editor.apply()
        }
    }

    public override fun onResume() {
        super.onResume()

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this@BoxActivity)
        val editor = sharedPreferences.edit()

        editor.remove("selected_box_id")
        editor.apply()

        getBoxes()
        syncBoxes()
    }

    public override fun onDestroy() {
        super.onDestroy()

        mRealm?.close()
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

        if (id == R.id.action_box_info) {
            val intent = Intent(this, BoxInfoActivity::class.java)
            intent.putExtra("box_id", mBox!!.id)
            startActivity(intent)
            return true
        } else if (id == R.id.action_box_sync) {
            syncBoxes()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        val box = mBoxes?.firstOrNull { id == it.id }

        if (box != null) {
            mBox = box
            setFoods(box)
        } else {
            val intent: Intent
            when (id) {
                R.id.nav_settings -> {
                    intent = Intent(this, SettingsActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_signout -> signOut()
                R.id.nav_units -> {
                    intent = Intent(this, UnitsActivity::class.java)
                    startActivity(intent)
                }
            }
        }

        mDrawer.closeDrawer(GravityCompat.START)

        return true
    }

    private fun getBoxes() {
        mRealm?.let { realm ->
            mBoxes = realm.where(Box::class.java).findAll()
            mNavigationView.setNavigationItemSelectedListener(this@BoxActivity)
            val menu = mNavigationView.menu.findItem(R.id.menu_boxes)
            menu.subMenu.clear()

            showProgressBar()
            realm.executeTransaction {
                mBoxes!!.forEach { menu.subMenu.add(Menu.NONE, it.id, Menu.NONE, it.name) }

                if (mBoxes!!.isNotEmpty()) {
                    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this@BoxActivity)
                    val boxIndex = sharedPreferences.getInt("selected_box_index", 0)
                    mBox = mBoxes!![boxIndex]
                    setFoods(mBox!!)
                }
            }
            hideProgressBar()
        }

    }

    private fun syncBoxes() {
        RetrofitFactory.getClient(BoxService::class.java, this).getBoxes()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
//                .doOnSubscribe { showProgressBar() }
//                .doOnUnsubscribe { hideProgressBar() }
                .subscribe(object: Subscriber<List<Box>>() {
                    override fun onError(e: Throwable?) {
                        e?.let {
                            Toast.makeText(this@BoxActivity, it.message, Toast.LENGTH_LONG).show()
                        }
                        signOut()
                    }

                    override fun onCompleted() {
                        Log.d(TAG, "Boxes synced.")
                    }

                    override fun onNext(boxes: List<Box>) {
                        mRealm?.let { realm ->
                            realm.where(Box::class.java).findAll().forEach { box->
                                realm.executeTransaction {
                                    box.deleteFromRealm()
                                }
                            }

                            boxes.forEach { box ->
                                realm.executeTransaction { realm.copyToRealmOrUpdate(box) }
                            }
                        }

                        mBoxes = boxes
                        setBoxesOnNavigation(boxes)
                    }

                })
    }

    private fun setNavigationHeader() {
        super.onStart()

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        mNavigationView.setNavigationItemSelectedListener(this)

        val headerView = mNavigationView.getHeaderView(0)
        val nameTextView = headerView.findViewById<TextView>(R.id.nameNavHeaderTextView)
        val mailTextView = headerView.findViewById<TextView>(R.id.mailNavHeaderTextView)
        val avatarImageView = headerView.findViewById<ImageView>(R.id.avatarNavHeaderImageView)

        val name = sharedPreferences.getString("name", "name")
        val mail = sharedPreferences.getString("mail", "mail")
        val avatarUrl = sharedPreferences.getString("avatar", null)

        nameTextView.text = name
        mailTextView.text = mail

        if (avatarUrl != null) {
            ImageDownloadTask(avatarImageView).execute(avatarUrl)
        }
    }

    private fun setBoxesOnNavigation(boxes: List<Box>) {
        val menu = mNavigationView.menu.findItem(R.id.menu_boxes)

        mNavigationView.setNavigationItemSelectedListener(this@BoxActivity)
        menu.subMenu.clear()
        boxes.forEach { menu.subMenu.add(Menu.NONE, it.id, Menu.NONE, it.name) }

        if (boxes.isNotEmpty()) {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this@BoxActivity)
            val boxIndex = sharedPreferences.getInt("selected_box_index", 0)
            mBox = boxes[boxIndex]
            mBox?.let {
                setFoods(it)
            }
        }
    }

    private fun setFoods(box: Box) {
        val foodListAdapter = FoodListAdapter(this, box.foods!!)

        mListView.adapter = foodListAdapter
        foodListAdapter.mode = Attributes.Mode.Single

        mListView.onItemClickListener = AdapterView.OnItemClickListener { parent, _, position, _ ->
            val food = parent.getItemAtPosition(position) as Food
            val intent = Intent(this@BoxActivity, FoodActivity::class.java)

            intent.putExtra("food_id", food.id)
            intent.putExtra("box_id", mBox!!.id)
            startActivityForResult(intent, EDIT_FOOD_REQUEST_CODE)
        }

        mListView.onItemLongClickListener = AdapterView.OnItemLongClickListener { parent, _, position, _ ->
            val food = parent.getItemAtPosition(position) as Food
            val options = arrayOf("Show", "Remove", "Cancel")
            val fragment = OptionsPickerDialogFragment.newInstance(food.name!!, options, food.id)

            fragment.setTargetFragment(null, FOOD_OPTIONS_REQUEST_CODE)
            fragment.show(fragmentManager, "food_option")

            true
        }

        mListView.setOnScrollListener(object : AbsListView.OnScrollListener {
            override fun onScrollStateChanged(view: AbsListView, scrollState: Int) { }

            override fun onScroll(view: AbsListView, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) { }
        })
    }

    private fun showProgressBar() {
        mProgressBar.visibility = View.VISIBLE
        mFab.hide()
    }

    private fun hideProgressBar() {
        mProgressBar.visibility = View.GONE
        mFab.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_OK || data == null) return

        when (requestCode) {
            ADD_FOOD_REQUEST_CODE -> {
                val foodId = data.getIntExtra("food_id", 0)
                val food = mRealm?.where(Food::class.java)?.equalTo("id", foodId)?.findFirst()
                val foodListAdapter = mListView.adapter as FoodListAdapter

                Log.e(TAG, food.toString())

                if (food != null) {
                    mRealm?.let { realm ->
                        realm.executeTransaction {
                            foodListAdapter.add(food)
                        }
                    }
                    foodListAdapter.notifyDataSetChanged()
                }
            }
            EDIT_FOOD_REQUEST_CODE -> {
                val foodId = data.getIntExtra("food_id", 0)
                val food = mRealm?.where(Food::class.java)?.equalTo("id", foodId)?.findFirst()

                Snackbar.make(mListView, "${food!!.name!!} Updated successfully", Snackbar.LENGTH_LONG)
                        .setAction("Dismiss", null)
                        .show()
            }
            FOOD_OPTIONS_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    val option = data.getIntExtra("option", -1)

                    when(option) {
                        0 -> {
                            val foodId = data.getIntExtra("food_id", 0)
                            val intent = Intent(this, FoodActivity::class.java)

                            intent.apply {
                                putExtra("food_id", foodId)
                                putExtra("box_id", mBox!!.id)
                            }
                            startActivity(intent)
                        }
                        1 -> {
                            val foodId = data.getIntExtra("food_id", 0)
                            val food = mRealm?.where(Food::class.java)?.equalTo("id", foodId)?.findFirst()

                            food?.let { removeFood(it) }
                        }
                        else -> return
                    }
                }
            }
        }
    }

    private fun removeFood(food: Food) {
        RetrofitFactory.getClient(FoodService::class.java, this)
                .remove(food.id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object: Subscriber<Void>() {
                    override fun onNext(t: Void?) {
                        val foodListAdapter = mListView.adapter as FoodListAdapter

                        mRealm?.let { realm ->
                            realm.executeTransaction {
                                foodListAdapter.remove(food)
                            }
                        }
                        foodListAdapter.notifyDataSetChanged()
                        Snackbar.make(mListView, "Removed successfully", Snackbar.LENGTH_LONG)
                                .setAction("Dismiss", null)
                                .show()
                    }

                    override fun onCompleted() {
                    }

                    override fun onError(e: Throwable?) {
                        e ?: return

                        Toast.makeText(this@BoxActivity, e.message, Toast.LENGTH_LONG).show()
                    }
                })
    }

    private fun signOut() {
        val editor = PreferenceManager.getDefaultSharedPreferences(this).edit()
        editor.clear()
        editor.apply()

        val intent = Intent(this@BoxActivity, SigninActivity::class.java)
        startActivity(intent)
    }

    companion object {
        private val TAG = "BoxActivity"
        private val ADD_FOOD_REQUEST_CODE = 1
        private val FOOD_OPTIONS_REQUEST_CODE = 101
        private val EDIT_FOOD_REQUEST_CODE = 102
    }
}
