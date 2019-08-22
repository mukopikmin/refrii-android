package com.refrii.client.unitlist

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.MenuItem
import android.view.View
import android.widget.*
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
    @BindView(R.id.emptyUnitMessage)
    lateinit var mEmptyUnitMessage: View
    @BindView(R.id.addButton)
    lateinit var mAddUnitButton: Button

    @Inject
    lateinit var mPresenter: UnitListPresenter

    private lateinit var mPreference: SharedPreferences

    private val onItemClickListActivity = AdapterView.OnItemClickListener { adapterView, _, i, _ ->
        val unit = adapterView.getItemAtPosition(i) as Unit
        val intent = Intent(this@UnitListActivity, UnitActivity::class.java)

        intent.putExtra("unit_id", unit.id)
        startActivity(intent)
    }
    private val onItemLongClickListener = AdapterView.OnItemLongClickListener { adapterView, _, i, _ ->
        val unit = adapterView.getItemAtPosition(i) as Unit
        val options = arrayOf(getString(R.string.message_show_detail), getString(R.string.message_delete), getString(R.string.message_cancel))
        val fragment = OptionsPickerDialogFragment.newInstance(unit.label!!, options, unit.id)

        fragment.setTargetFragment(null, UNIT_OPTIONS_REQUEST_CODE)
        fragment.show(supportFragmentManager, "unit_option")

        true
    }

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

        mPreference = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        listView.onItemClickListener = onItemClickListActivity
        listView.onItemLongClickListener = onItemLongClickListener

        fab.setOnClickListener { addUnit() }
        mAddUnitButton.setOnClickListener { addUnit() }

        hideEmptyMessage()
    }

    private fun addUnit() {
        val intent = Intent(this@UnitListActivity, NewUnitActivity::class.java)

        startActivityForResult(intent, NEW_UNIT_REQUEST_CODE)
    }

    override fun onStart() {
        super.onStart()

        mPresenter.takeView(this)
    }

    override fun onResume() {
        super.onResume()

        val preference = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val userId = preference.getInt(getString(R.string.preference_key_id), -1)

        mPresenter.getUnits(userId)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val preference = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val userId = preference.getInt(getString(R.string.preference_key_id), -1)

        if (resultCode != Activity.RESULT_OK) return
        data ?: return

        when (requestCode) {
            NEW_UNIT_REQUEST_CODE -> mPresenter.getUnits(userId)
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
                    1 -> mPresenter.removeUnit(unitId, userId)
                    // Cancel
                    else -> return
                }
            }
        }
    }

    override fun showEmptyMessage() {
        mEmptyUnitMessage.visibility = View.VISIBLE
    }

    override fun hideEmptyMessage() {
        mEmptyUnitMessage.visibility = View.GONE
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
