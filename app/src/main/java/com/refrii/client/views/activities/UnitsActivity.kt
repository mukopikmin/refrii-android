package com.refrii.client.views.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.widget.AdapterView
import android.widget.ListView
import android.widget.Toast
import com.refrii.client.R
import com.refrii.client.models.Food
import com.refrii.client.models.Unit
import com.refrii.client.services.RetrofitFactory
import com.refrii.client.services.UnitService
import com.refrii.client.utils.RealmUtil
import com.refrii.client.views.adapters.UnitListAdapter
import com.refrii.client.views.fragments.OptionsPickerDialogFragment
import io.realm.Realm
import kotterknife.bindView
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class UnitsActivity : AppCompatActivity() {

    private val toolbar: Toolbar by bindView(R.id.toolbar)
    private val listView: ListView by bindView(R.id.listView)
    private val fab: FloatingActionButton by bindView(R.id.fab)

    private lateinit var mRealm: Realm
    private var mUnits: MutableList<Unit>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_units)
        setSupportActionBar(toolbar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeButtonEnabled(true)
        }

        Realm.init(this)
        mRealm = RealmUtil.getInstance()

        fab.setOnClickListener {
            val intent = Intent(this@UnitsActivity, NewUnitActivity::class.java)
            startActivityForResult(intent, NEW_UNIT_REQUEST_CODE)
        }

        listView.onItemClickListener = AdapterView.OnItemClickListener { adapterView, _, i, _ ->
            val unit = adapterView.getItemAtPosition(i) as Unit
            val intent = Intent(this@UnitsActivity, UnitActivity::class.java)

            intent.putExtra("unit_id", unit.id)
            startActivity(intent)
        }

        listView.onItemLongClickListener = AdapterView.OnItemLongClickListener { adapterView, _, i, _ ->
            val unit = adapterView.getItemAtPosition(i) as Unit
            val options = arrayOf("Show", "Remove", "Cancel")
            val fragment = OptionsPickerDialogFragment.newInstance(unit.label!!, options, unit.id)

            fragment.setTargetFragment(null, UNIT_OPTIONS_REQUEST_CODE)
            fragment.show(fragmentManager, "unit_option")

            true
        }
    }

    override fun onStart() {
        super.onStart()

        getUnits()
        syncUnits()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) return
        data ?: return

        when (requestCode) {
            NEW_UNIT_REQUEST_CODE -> {
                val unit = data.getSerializableExtra("unit") as Unit

                mUnits?.let {
                    it.add(unit)
                    listView.deferNotifyDataSetChanged()
                }
            }
            UNIT_OPTIONS_REQUEST_CODE -> {
                val option = data.getIntExtra("option", -1)
                val unitId = data.getIntExtra("target_id", 0)

                when(option) {
                    0 -> {
                        val intent = Intent(this@UnitsActivity, UnitActivity::class.java)

                        intent.putExtra("unit_id", unitId)
                        startActivity(intent)
                    }
                    1 -> {
                        val unit = mRealm.where(Unit::class.java).equalTo("id", unitId).findFirst()

                        unit?.let { removeUnit(it) }
                    }
                    else -> return
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        var result = true

        when (id) {
            android.R.id.home -> finish()
            else -> result = super.onOptionsItemSelected(item)
        }

        return result
    }

    private fun getUnits(): List<Unit>? {
        val units = mRealm.where(Unit::class.java)?.findAll()

        units?.let {
            setUnits(it)
            mUnits = it
        }

        return units
    }

    private fun syncUnits() {
        RetrofitFactory.getClient(UnitService::class.java, this@UnitsActivity)
                .getUnits()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object: Subscriber<List<Unit>>() {
                    override fun onError(e: Throwable) {
                        Toast.makeText(this@UnitsActivity, e.message, Toast.LENGTH_LONG).show()
                    }

                    override fun onCompleted() {
                    }

                    override fun onNext(t: List<Unit>) {
                        mRealm.executeTransaction {
                            t.forEach { mRealm.copyToRealmOrUpdate(it) }
                        }
                        mUnits?.let { setUnits(it) }
                        mUnits = t.toMutableList()
                    }
                })
    }

    private fun removeUnit(unit: Unit) {
        val foods = mRealm.where(Food::class.java).equalTo("unit.id", unit.id).findAll()
        val label = unit.label

        if (foods.size > 0) {
            Snackbar.make(listView, "$label is used by some foods. Can not remove.", Snackbar.LENGTH_LONG).show()
            return
        }

        RetrofitFactory.getClient(UnitService::class.java, this@UnitsActivity)
                .deleteUnit(unit.id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object: Subscriber<Void>() {
                    override fun onCompleted() {
                        val unitListAdapter = listView.adapter as UnitListAdapter

                        unitListAdapter.notifyDataSetChanged()
                        Snackbar.make(listView, "$label is removed.", Snackbar.LENGTH_LONG).show()
                    }

                    override fun onError(e: Throwable) {
                        Toast.makeText(this@UnitsActivity, e.message, Toast.LENGTH_LONG).show()
                    }

                    override fun onNext(t: Void?) {
                        mRealm.executeTransaction { unit.deleteFromRealm() }
                    }
                })
    }

    private fun setUnits(units: List<Unit>) {
        val adapter = UnitListAdapter(this@UnitsActivity, units)

        listView.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    companion object {
        private const val TAG = "UnitsActivity"
        private const val NEW_UNIT_REQUEST_CODE = 101
        private const val UNIT_OPTIONS_REQUEST_CODE = 102
    }
}
