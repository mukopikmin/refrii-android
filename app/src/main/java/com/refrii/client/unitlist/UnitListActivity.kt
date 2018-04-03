package com.refrii.client.unitlist

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.Toast
import com.refrii.client.App
import com.refrii.client.R
import com.refrii.client.data.api.models.Unit
import com.refrii.client.dialogs.OptionsPickerDialogFragment
import com.refrii.client.newunit.NewUnitActivity
import com.refrii.client.unit.UnitActivity
import kotterknife.bindView
import javax.inject.Inject

class UnitListActivity : AppCompatActivity(), UnitListContract.View {

    private val toolbar: Toolbar by bindView(R.id.toolbar)
    private val listView: ListView by bindView(R.id.listView)
    private val fab: FloatingActionButton by bindView(R.id.fab)
    private val progressBar: ProgressBar by bindView(R.id.progressBar)

    @Inject
    lateinit var mPresenter: UnitListPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (application as App).getComponent().inject(this)

        setContentView(R.layout.activity_units)

        setSupportActionBar(toolbar)
        supportActionBar?.let {
            it.title = "Unit list"
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeButtonEnabled(true)
        }

        fab.setOnClickListener {
            val intent = Intent(this@UnitListActivity, NewUnitActivity::class.java)
            startActivityForResult(intent, NEW_UNIT_REQUEST_CODE)
        }

        listView.onItemClickListener = AdapterView.OnItemClickListener { adapterView, _, i, _ ->
            val unit = adapterView.getItemAtPosition(i) as Unit
            val intent = Intent(this@UnitListActivity, UnitActivity::class.java)

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

        mPresenter.takeView(this)
        hideProgressBar()
    }

    override fun onResume() {
        super.onResume()

        val preference = PreferenceManager.getDefaultSharedPreferences(this)
        val userId = preference.getInt("id", 0)

        mPresenter.getUnits(userId)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) return
        data ?: return

        when (requestCode) {
            NEW_UNIT_REQUEST_CODE -> {
                val unit = data.getSerializableExtra("unit") as Unit

                mPresenter.mUnits?.let {
                    it.add(unit)
                    listView.deferNotifyDataSetChanged()

                    showSnackbar("Unit created")
                }
            }
            UNIT_OPTIONS_REQUEST_CODE -> {
                val option = data.getIntExtra("option", -1)
                val unitId = data.getIntExtra("target_id", 0)

                when (option) {
                    0 -> {
                        val intent = Intent(this@UnitListActivity, UnitActivity::class.java)

                        intent.putExtra("unit_id", unitId)
                        startActivity(intent)
                    }
                    1 -> mPresenter.removeUnit(unitId)
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

    override fun setUnits(units: List<Unit>) {
        val adapter = UnitListAdapter(this@UnitListActivity, units)

        listView.adapter = adapter
    }

    override fun showProgressBar() {
        progressBar.visibility = View.VISIBLE
    }

    override fun hideProgressBar() {
        progressBar.visibility = View.GONE
    }

    override fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun showSnackbar(message: String) {
        Snackbar.make(listView, message, Snackbar.LENGTH_LONG).show()
    }

    companion object {
        @Suppress("unused")
        private const val TAG = "UnitListActivity"
        private const val NEW_UNIT_REQUEST_CODE = 101
        private const val UNIT_OPTIONS_REQUEST_CODE = 102
    }
}
