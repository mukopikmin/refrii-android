package com.refrii.client.views.activities

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
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.daimajia.swipe.util.Attributes
import com.refrii.client.R
import com.refrii.client.models.Box
import com.refrii.client.models.Food
import com.refrii.client.services.BoxService
import com.refrii.client.services.FoodService
import com.refrii.client.services.RetrofitFactory
import com.refrii.client.tasks.ImageDownloadTask
import com.refrii.client.tasks.ImageDownloadTaskCallback
import com.refrii.client.utils.RealmUtil
import com.refrii.client.views.adapters.FoodRecyclerViewAdapter
import com.refrii.client.views.fragments.OptionsPickerDialogFragment
import io.realm.Realm
import kotterknife.bindView
import okhttp3.MultipartBody
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class BoxActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val mToolbar: Toolbar by bindView(R.id.toolbar)
    private val mFab: FloatingActionButton by bindView(R.id.fab)
    private val mDrawer: DrawerLayout by bindView(R.id.drawer_layout)
    private val mNavigationView: NavigationView by bindView(R.id.nav_view)
    private val mRecyclerView: RecyclerView by bindView(R.id.recyclerView)
    private val mProgressBar: ProgressBar by bindView(R.id.progressBar)

    private var mBoxes: List<Box>? = null
    private var mBox: Box? = null
    private lateinit var mRealm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_box)
        setSupportActionBar(mToolbar)

        hideProgressBar()

        val toggle = ActionBarDrawerToggle(this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        mDrawer.addDrawerListener(toggle)
        toggle.syncState()

        Realm.init(this)
        mRealm = RealmUtil.getInstance()

        mRecyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        mRecyclerView.layoutManager = LinearLayoutManager(this)

        mFab.setOnClickListener {
            mBox?.let {
                val intent = Intent(this@BoxActivity, NewFoodActivity::class.java)

                intent.putExtra("boxId", it.id)
                startActivityForResult(intent, ADD_FOOD_REQUEST_CODE)
            }
        }
    }

    public override fun onStart() {
        super.onStart()

        setNavigationHeader()
    }

    public override fun onResume() {
        super.onResume()

        val editor = PreferenceManager.getDefaultSharedPreferences(this@BoxActivity).edit()

        editor.remove("selected_box_id")
        editor.apply()

        getBoxes()
        syncBoxes()
    }

    public override fun onPause() {
        super.onPause()

        mBox?.let { box ->
            mBoxes?.let { boxes ->
                val editor = PreferenceManager.getDefaultSharedPreferences(this@BoxActivity).edit()
                editor.putInt("selected_box_index", boxes.indexOf(box))
                editor.apply()
            }
        }
    }

    public override fun onRestart() {
        super.onRestart()

        getBoxes()
    }

    public override fun onDestroy() {
        super.onDestroy()

        mRealm.close()
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
                mBox?.let {
                    val intent = Intent(this, BoxInfoActivity::class.java)

                    intent.putExtra("box_id", it.id)
                    startActivity(intent)
                }

                return true
            }
            R.id.action_box_sync -> {
                syncBoxes()

                return true
            }
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
            when (id) {
                R.id.nav_settings -> {
                    val intent = Intent(this, SettingsActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_signout -> signOut()
                R.id.nav_units -> {
                    val intent = Intent(this, UnitsActivity::class.java)
                    startActivity(intent)
                }
            }
        }

        mDrawer.closeDrawer(GravityCompat.START)

        return true
    }

    private fun getBoxes() {
        mBoxes = mRealm.where(Box::class.java).findAll()
        mNavigationView.setNavigationItemSelectedListener(this@BoxActivity)
        val menu = mNavigationView.menu.findItem(R.id.menu_boxes)
        menu.subMenu.clear()

        mRealm.executeTransaction {
            mBoxes?.forEach { menu.subMenu.add(Menu.NONE, it.id, Menu.NONE, it.name) }

            mBoxes?.let {
                if (it.isNotEmpty()) {
                    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this@BoxActivity)
                    val boxIndex = sharedPreferences.getInt("selected_box_index", 0)
                    mBox = it[boxIndex]
                    mBox?.let { setFoods(it) }
                }
            }
        }
    }

    private fun syncBoxes() {
        RetrofitFactory.getClient(BoxService::class.java, this).getBoxes()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { showProgressBar() }
                .doOnUnsubscribe { hideProgressBar() }
                .subscribe(object: Subscriber<List<Box>>() {
                    override fun onError(e: Throwable) {
                        Log.e(TAG, e.message)
                        Toast.makeText(this@BoxActivity, e.message, Toast.LENGTH_LONG).show()

                        signOut()
                    }

                    override fun onCompleted() {
                        Log.d(TAG, "Boxes synced.")
                    }

                    override fun onNext(boxes: List<Box>) {
                        mRealm.executeTransaction { realm ->
                            boxes.forEach { realm.copyToRealmOrUpdate(it) }
                        }

                        mBoxes = boxes
                        mRecyclerView.adapter.notifyDataSetChanged()
                    }
                })
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

    private fun setFoods(box: Box) {
        val adapter = FoodRecyclerViewAdapter(this, box.foods!!)

        adapter.apply {
            mClickListener = View.OnClickListener {
                val intent = Intent(this@BoxActivity, FoodActivity::class.java)
                val position = mRecyclerView.getChildAdapterPosition(it)
                val food = adapter.getItemAtPosition(position)

                intent.putExtra("food_id", food.id)
                intent.putExtra("box_id", box.id)
                startActivityForResult(intent, EDIT_FOOD_REQUEST_CODE)
            }

            mLongClickListener = View.OnLongClickListener {
                val position = mRecyclerView.getChildAdapterPosition(it)
                val food = adapter.getItemAtPosition(position)

                food.name?.let {
                    val options = arrayOf("Show", "Remove", "Cancel")
                    val fragment = OptionsPickerDialogFragment.newInstance(it, options, food.id)

                    fragment.setTargetFragment(null, FOOD_OPTIONS_REQUEST_CODE)
                    fragment.show(fragmentManager, "food_option")
                }

                true
            }

            mIncrementClickListener = View.OnClickListener {
                val position = mRecyclerView.getChildAdapterPosition(it)
                val food = adapter.getItemAtPosition(position)

                mRealm.executeTransaction {
                    food.amount = food.amount + 1
                    syncFood(food)
                }

                notifyItemChanged(position)
            }

            mDecrementClickListener = View.OnClickListener {
                val position = mRecyclerView.getChildAdapterPosition(it)
                val food = adapter.getItemAtPosition(position)

                mRealm.executeTransaction {
                    food.amount = food.amount - 1
                    syncFood(food)
                }

                notifyItemChanged(position)
            }

            mode = Attributes.Mode.Single
        }
        mRecyclerView.adapter = adapter
    }

    private fun syncFood(food: Food) {
        val body = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("amount", food.amount.toString())
                .build()

        RetrofitFactory.getClient(FoodService::class.java, this)
                .updateFood(food.id, body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Subscriber<Food>() {
                    override fun onError(e: Throwable) {
                        Toast.makeText(this@BoxActivity, e.message, Toast.LENGTH_LONG).show()
                    }

                    override fun onCompleted() {
                        Log.d(TAG, "Update completed.")
                    }

                    override fun onNext(t: Food) {}
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
                val food = mRealm.where(Food::class.java)?.equalTo("id", foodId)?.findFirst()
                val adapter = mRecyclerView.adapter as FoodRecyclerViewAdapter

                food?.let {
                    mRealm.executeTransaction { adapter.add(food) }
                    adapter.notifyDataSetChanged()
                }
            }
            EDIT_FOOD_REQUEST_CODE -> {
                val foodId = data.getIntExtra("food_id", 0)
                val food = mRealm.where(Food::class.java)?.equalTo("id", foodId)?.findFirst()

                food?.name?.let { Snackbar.make(mRecyclerView, "$it updated successfully", Snackbar.LENGTH_LONG).show() }
            }
            FOOD_OPTIONS_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    val option = data.getIntExtra("option", -1)

                    when(option) {
                        0 -> {
                            val foodId = data.getIntExtra("target_id", 0)
                            val intent = Intent(this, FoodActivity::class.java)

                            intent.apply {
                                putExtra("food_id", foodId)
                                putExtra("box_id", mBox!!.id)
                            }
                            startActivity(intent)
                        }
                        1 -> {
                            val foodId = data.getIntExtra("target_id", 0)
                            val food = mRealm.where(Food::class.java)?.equalTo("id", foodId)?.findFirst()

                            food?.let { removeFood(it) }
                        }
                        else -> return
                    }
                }
            }
        }
    }

    private fun removeFood(food: Food) {
        val adapter = mRecyclerView.adapter as FoodRecyclerViewAdapter

        mRealm.executeTransaction {
            adapter.remove(food)
            adapter.notifyDataSetChanged()
            removeFoodSync(food)

            Snackbar.make(mRecyclerView, "${food.name} is removed.", Snackbar.LENGTH_LONG).show()
        }
    }

    private fun removeFoodSync(food: Food) {
        RetrofitFactory.getClient(FoodService::class.java, this)
                .remove(food.id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object: Subscriber<Void>() {
                    override fun onNext(t: Void?) {}

                    override fun onCompleted() {
                        Log.d(TAG, "Food removed successfully.")
                    }

                    override fun onError(e: Throwable) {
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
        private const val TAG = "BoxActivity"
        private const val ADD_FOOD_REQUEST_CODE = 101
        private const val FOOD_OPTIONS_REQUEST_CODE = 102
        private const val EDIT_FOOD_REQUEST_CODE = 103
    }
}
