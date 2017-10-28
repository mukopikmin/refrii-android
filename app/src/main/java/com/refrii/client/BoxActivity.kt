package com.refrii.client

import android.app.Activity
import android.app.Dialog
import android.app.DialogFragment
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
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
import android.widget.AbsListView
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.TextView

import com.daimajia.swipe.util.Attributes

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BoxActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var mBoxes: List<Box>? = null
    private var mBox: Box? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_box)

        val toolbar = findViewById(R.id.toolbar) as Toolbar
        val fab = findViewById(R.id.fab) as FloatingActionButton

        setSupportActionBar(toolbar)

        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        val toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        fab.setOnClickListener {
            val intent = Intent(this@BoxActivity, NewFoodActivity::class.java)
            intent.putExtra("boxId", mBox!!.id)
            startActivityForResult(intent, REQUEST_CODE)
        }

        showProgressBar()
    }

    public override fun onStart() {
        super.onStart()

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        val navigationView = findViewById(R.id.nav_view) as NavigationView
        navigationView.setNavigationItemSelectedListener(this)

        val headerView = navigationView.getHeaderView(0)
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

        getBoxes()

        editor.remove("selected_box_id")
        editor.apply()
    }

    override fun onBackPressed() {
        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.box, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId


        if (id == R.id.action_box_info) {
            val intent = Intent(this@BoxActivity, BoxInfoActivity::class.java)
            intent.putExtra("box", mBox)
            startActivity(intent)
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        val boxIndex = mBoxes!!.indexOf(Box(id))

        if (boxIndex != -1) {
            mBox = mBoxes!![boxIndex]
            setFoods(mBox!!)
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

        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    private fun getBoxes() {
        val service = RetrofitFactory.getClient<BoxService>(BoxService::class.java, this@BoxActivity)
        val call = service.boxes
        call.enqueue(object : BasicCallback<List<Box>>(this@BoxActivity) {
            override fun onResponse(call: Call<List<Box>>, response: Response<List<Box>>) {
                super.onResponse(call, response)

                if (response.code() == 200) {
                    val navigationView = findViewById(R.id.nav_view) as NavigationView
                    navigationView.setNavigationItemSelectedListener(this@BoxActivity)
                    val menu = navigationView.menu.findItem(R.id.menu_boxes)
                    menu.subMenu.clear()

                    mBoxes = response.body()
                    for (box in mBoxes!!) {
                        menu.subMenu.add(Menu.NONE, box.id, Menu.NONE, box.name)
                    }
                    if (mBoxes!!.size > 0) {
                        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this@BoxActivity)
                        val boxIndex = sharedPreferences.getInt("selected_box_index", 0)
                        mBox = mBoxes!![boxIndex]
                        setFoods(mBox!!)
                    }

                    hideProgressBar()
                }
            }
        })
    }

    private fun setFoods(box: Box) {
        val mFoodListAdapter = FoodListAdapter(this, box.foods!!)
        val mListView = findViewById(R.id.listView) as ListView

        mListView.adapter = mFoodListAdapter
        mFoodListAdapter.mode = Attributes.Mode.Single
        mListView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val food = parent.getItemAtPosition(position) as Food
            val intent = Intent(this@BoxActivity, FoodActivity::class.java)
            intent.putExtra("foodId", food.id)
            startActivity(intent)
        }
        mListView.setOnTouchListener { v, event ->
            Log.e("ListView", "OnTouch")
            false
        }
        mListView.onItemLongClickListener = AdapterView.OnItemLongClickListener { parent, view, position, id ->
            val fragment = FoodOptionDialogFragment()
            fragment.setFood(parent.getItemAtPosition(position) as Food)
            fragment.setFoodListAdapter(mFoodListAdapter)
            fragment.setListView(mListView)
            fragment.show(fragmentManager, "food_option")
            true
        }
        mListView.setOnScrollListener(object : AbsListView.OnScrollListener {
            override fun onScrollStateChanged(view: AbsListView, scrollState: Int) {
                Log.e("ListView", "onScrollStateChanged")
            }

            override fun onScroll(view: AbsListView, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {

            }
        })

        mListView.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                Log.e("ListView", "onItemSelected:" + position)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                Log.e("ListView", "onNothingSelected:")
            }
        }
    }

    private fun showProgressBar() {
        val mProgressBar = findViewById(R.id.progressBar) as ProgressBar
        val mFab = findViewById(R.id.fab) as FloatingActionButton

        mProgressBar.visibility = View.VISIBLE
        mFab.hide()
    }

    private fun hideProgressBar() {
        val mProgressBar = findViewById(R.id.progressBar) as ProgressBar
        val mFab = findViewById(R.id.fab) as FloatingActionButton

        mProgressBar.visibility = View.GONE
        mFab.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val food = data!!.getSerializableExtra("food") as Food
                val mListView = findViewById(R.id.listView) as ListView
                val mFoodListAdapter = mListView.adapter as FoodListAdapter

                mFoodListAdapter.add(food)
                mFoodListAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun signOut() {
        val editor = PreferenceManager.getDefaultSharedPreferences(this).edit()
        editor.clear()
        editor.apply()

        val intent = Intent(this@BoxActivity, SigninActivity::class.java)
        startActivity(intent)
    }

    class FoodOptionDialogFragment : DialogFragment() {
        private var mFood: Food? = null
        private var mFoodListAdapter: FoodListAdapter? = null
        private var mListView: ListView? = null

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val items = arrayOf<CharSequence>("Show", "Remove", "Cancel")
            val activity = activity

            val builder = AlertDialog.Builder(activity)
                    .setTitle(mFood!!.name)
                    .setItems(items) { dialog, which ->
                        when (which) {
                            0 -> {
                                val intent = Intent(activity, FoodActivity::class.java)
                                intent.putExtra("foodId", mFood!!.id)
                                startActivity(intent)
                            }
                            1 -> {
                                val service = RetrofitFactory.getClient(FoodService::class.java, activity)
                                val call = service.remove(mFood!!.id)
                                call.enqueue(object : Callback<Void> {
                                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                                        if (response.code() == 204) {
                                            mFoodListAdapter!!.remove(mFood!!)
                                            mFoodListAdapter!!.notifyDataSetChanged()
                                            Snackbar.make(mListView!!, "Removed successfully", Snackbar.LENGTH_LONG)
                                                    .setAction("Dismiss", null)
                                                    .show()
                                        }
                                    }

                                    override fun onFailure(call: Call<Void>, t: Throwable) {

                                    }
                                })
                            }
                            else -> {
                            }
                        }
                    }

            return builder.create()
        }

        fun setFood(food: Food) {
            mFood = food
        }

        fun setFoodListAdapter(foodListAdapter: FoodListAdapter?) {
            mFoodListAdapter = foodListAdapter
        }

        fun setListView(listView: ListView?) {
            mListView = listView
        }
    }

    companion object {
        private val TAG = "BoxActivity"
        private val REQUEST_CODE = 1
    }
}
