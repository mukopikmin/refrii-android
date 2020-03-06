package app.muko.mypantry.unit

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import app.muko.mypantry.App
import app.muko.mypantry.R
import app.muko.mypantry.data.models.Unit
import butterknife.BindView
import butterknife.ButterKnife
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class UnitActivity : AppCompatActivity(), UnitContract.View {

    @BindView(R.id.toolbar)
    lateinit var mToolbar: Toolbar
    @BindView(R.id.labelEditText)
    lateinit var mLabelEditText: EditText
    @BindView(R.id.stepEditText)
    lateinit var mStepEditText: EditText
    @BindView(R.id.createdTextView)
    lateinit var mCreated: TextView
    @BindView(R.id.updatedTextView)
    lateinit var mUpdated: TextView
    @BindView(R.id.fab)
    lateinit var mFab: FloatingActionButton
    @BindView(R.id.progressBar)
    lateinit var mProgressBar: ProgressBar

    @Inject
    lateinit var mPresenter: UnitPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (application as App).getComponent().inject(this)

        setContentView(R.layout.activity_unit)
        ButterKnife.bind(this)
        setSupportActionBar(mToolbar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeButtonEnabled(true)
        }

        mFab.setOnClickListener { updateUnit() }
    }

    private fun updateUnit() {
        val label = mLabelEditText.text.toString()
        val step = mStepEditText.text.toString().toDouble()

        mPresenter.updateUnit(label, step)
    }

    override fun onStart() {
        super.onStart()

        val intent = intent
        val unitId = intent.getIntExtra("unit_id", 0)

        mPresenter.takeView(this)
        mPresenter.getUnit(unitId)
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

    override fun setUnit(unit: Unit?) {
        unit ?: return

        val formatter = SimpleDateFormat(getString(R.string.format_datetime), Locale.getDefault())

        title = unit.label
        mLabelEditText.setText(unit.label)
        mStepEditText.setText(unit.step.toString())
        mCreated.text = formatter.format(unit.createdAt)
        mUpdated.text = formatter.format(unit.updatedAt)
    }

    override fun onLoading() {
        mProgressBar.visibility = View.VISIBLE
        mFab.hide()
    }

    override fun onLoaded() {
        mProgressBar.visibility = View.GONE
        mFab.show()

    }

    override fun showToast(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun showSnackbar(message: String?) {
        message ?: return

        Snackbar.make(mLabelEditText, message, Snackbar.LENGTH_LONG).show()
    }
}
