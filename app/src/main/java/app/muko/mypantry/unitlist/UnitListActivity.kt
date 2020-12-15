package app.muko.mypantry.unitlist

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import app.muko.mypantry.R
import app.muko.mypantry.data.models.Unit
import app.muko.mypantry.dialogs.OptionsPickerDialogFragment
import app.muko.mypantry.newunit.NewUnitActivity
import app.muko.mypantry.unit.UnitActivity
import butterknife.BindView
import butterknife.ButterKnife
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import dagger.android.AndroidInjection
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
    lateinit var emptyUnitListMessage: View

    @BindView(R.id.addButton)
    lateinit var addUnitButton: Button

    @Inject
    lateinit var presenter: UnitListPresenter

    @Inject
    lateinit var preference: SharedPreferences

    private val onItemClickListActivity = AdapterView.OnItemClickListener { adapterView, _, i, _ ->
        val unit = adapterView.getItemAtPosition(i) as Unit
        val intent = Intent(this@UnitListActivity, UnitActivity::class.java)

        intent.putExtra("unit_id", unit.id)
        startActivity(intent)
    }

    private val onItemLongClickListener = AdapterView.OnItemLongClickListener { adapterView, _, i, _ ->
        val unit = adapterView.getItemAtPosition(i) as Unit
        val options = arrayOf(
                getString(R.string.message_show_detail),
                getString(R.string.message_delete),
                getString(R.string.message_cancel)
        )
        val fragment = OptionsPickerDialogFragment.newInstance(unit.label!!, options, unit.id)

        fragment.setTargetFragment(null, UNIT_OPTIONS_REQUEST_CODE)
        fragment.show(supportFragmentManager, "unit_option")

        true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AndroidInjection.inject(this)
//        (application as App).getComponent().inject(this)

        setContentView(R.layout.activity_units)
        ButterKnife.bind(this)

        setSupportActionBar(toolbar)
        supportActionBar?.let {
            it.title = getString(R.string.title_unit_list)
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeButtonEnabled(true)
        }

        listView.onItemClickListener = onItemClickListActivity
        listView.onItemLongClickListener = onItemLongClickListener

        fab.setOnClickListener { addUnit() }
        addUnitButton.setOnClickListener { addUnit() }

        hideEmptyMessage()
    }

    private fun addUnit() {
        val intent = Intent(this@UnitListActivity, NewUnitActivity::class.java)

        startActivityForResult(intent, NEW_UNIT_REQUEST_CODE)
    }

    override fun onStart() {
        super.onStart()

        presenter.init(this)
    }

    override fun onResume() {
        super.onResume()

        val userId = preference.getInt(getString(R.string.preference_key_id), -1)

        presenter.getUnits(userId)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val userId = preference.getInt(getString(R.string.preference_key_id), -1)

        if (resultCode != Activity.RESULT_OK) return
        data ?: return

        when (requestCode) {
            NEW_UNIT_REQUEST_CODE -> presenter.getUnits(userId)
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
                    1 -> presenter.removeUnit(unitId)
                    // Cancel
                    else -> return
                }
            }
        }
    }

    override fun showEmptyMessage() {
        emptyUnitListMessage.visibility = View.VISIBLE
    }

    override fun hideEmptyMessage() {
        emptyUnitListMessage.visibility = View.GONE
    }

    override fun onUnitCreateCompleted(unit: Unit?) {
        val userId = preference.getInt(getString(R.string.preference_key_id), 0)
        val label = unit?.label ?: ""

        showSnackbar("単位 \"$label\" が追加されました")
        presenter.getUnits(userId)
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

        val userId = preference.getInt(getString(R.string.preference_key_id), -1)
        val ownUnits = units.filter { it.user?.id == userId }

        listView.adapter = UnitListAdapter(this@UnitListActivity, ownUnits)
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
