package com.refrii.client.unitlist

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import butterknife.BindView
import butterknife.ButterKnife
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.refrii.client.App
import com.refrii.client.R
import com.refrii.client.data.models.Unit
import com.refrii.client.dialogs.OptionsPickerDialogFragment
import com.refrii.client.newunit.NewUnitActivity
import com.refrii.client.unit.UnitActivity
import javax.inject.Inject

class UnitListActivity : AppCompatActivity(), UnitListContract.View {

    @BindView(R.id.toolbar)
    lateinit var toolbar: Toolbar
    @BindView(R.id.listView)
    lateinit var listView: ListView
    @BindView(R.id.fab)
    lateinit var fab: FloatingActionButton
    @BindView(R.id.progressBar)
    lateinit var progressBar: ProgressBar

    @Inject
    lateinit var mPresenter: UnitListPresenter

    private lateinit var mPreference: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (application as App).getComponent().inject(this)

        setContentView(R.layout.activity_units)
        ButterKnife.bind(this)

        setSupportActionBar(toolbar)
        supportActionBar?.let {
            it.title = getString(R.string.title_unit_list)
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeButtonEnabled(true)
        }

        mPreference = PreferenceManager.getDefaultSharedPreferences(this)

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
            val options = arrayOf(getString(R.string.message_show_detail), getString(R.string.message_delete), getString(R.string.message_cancel))
            val fragment = OptionsPickerDialogFragment.newInstance(unit.label!!, options, unit.id)

            fragment.setTargetFragment(null, UNIT_OPTIONS_REQUEST_CODE)
            fragment.show(supportFragmentManager, "unit_option")

            true
        }
    }

    override fun onStart() {
        super.onStart()

        mPresenter.takeView(this)
    }

    override fun onResume() {
        super.onResume()

        val preference = PreferenceManager.getDefaultSharedPreferences(this)
        val userId = preference.getInt(getString(R.string.preference_key_id), -1)

        mPresenter.getUnits(userId)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) return
        data ?: return

        when (requestCode) {
            NEW_UNIT_REQUEST_CODE -> {
                val unitId = data.getIntExtra(getString(R.string.key_unit_id), 0)

                mPresenter.getUnit(unitId)
            }
            UNIT_OPTIONS_REQUEST_CODE -> {
                val option = data.getIntExtra("option", -1)
                val unitId = data.getIntExtra("target_id", 0)

                when (option) {
                    // Show
                    0 -> {
                        val intent = Intent(this@UnitListActivity, UnitActivity::class.java)

                        intent.putExtra("unit_id", unitId)
                        startActivity(intent)
                    }
                    // Remove
                    1 -> {
                        val preference = PreferenceManager.getDefaultSharedPreferences(this)
                        val userId = preference.getInt(getString(R.string.preference_key_id), 0)

                        mPresenter.removeUnit(unitId, userId)
                    }
                    // Cancel
                    else -> return
                }
            }
        }
    }

    override fun onUnitCreateCompleted(unit: Unit?) {
        val userId = mPreference.getInt(getString(R.string.preference_key_id), 0)
        val label = unit?.label ?: ""

        showSnackbar("単位 \"$label\" が追加されました")
        mPresenter.getUnits(userId)
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

    override fun setUnits(units: List<Unit>?) {
        units ?: return

        listView.adapter = UnitListAdapter(this@UnitListActivity, units)
    }

    override fun showProgressBar() {
        progressBar.visibility = View.VISIBLE
    }

    override fun hideProgressBar() {
        progressBar.visibility = View.GONE
    }

    override fun showToast(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun showSnackbar(message: String?) {
        message ?: return
        Snackbar.make(listView, message, Snackbar.LENGTH_LONG).show()
    }

    companion object {
        private const val NEW_UNIT_REQUEST_CODE = 101
        private const val UNIT_OPTIONS_REQUEST_CODE = 102
    }
}
