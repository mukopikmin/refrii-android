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
import dagger.android.AndroidInjection
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


class UnitActivity : AppCompatActivity(), UnitContract.View {

    @BindView(R.id.toolbar)
    lateinit var toolbar: Toolbar

    @BindView(R.id.labelEditText)
    lateinit var labelEditText: EditText

    @BindView(R.id.stepEditText)
    lateinit var stepEditText: EditText

    @BindView(R.id.createdTextView)
    lateinit var createdAtText: TextView

    @BindView(R.id.updatedTextView)
    lateinit var updatedAtText: TextView

    @BindView(R.id.fab)
    lateinit var fab: FloatingActionButton

    @BindView(R.id.progressBar)
    lateinit var progressBar: ProgressBar

    @Inject
    lateinit var presenter: UnitPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AndroidInjection.inject(this)
//        (application as App).getComponent().inject(this)

        setContentView(R.layout.activity_unit)
        ButterKnife.bind(this)
        setSupportActionBar(toolbar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeButtonEnabled(true)
        }

        fab.setOnClickListener { updateUnit() }
    }

    private fun updateUnit() {
        val label = labelEditText.text.toString()
        val step = stepEditText.text.toString().toDouble()

        presenter.updateUnit(label, step)
    }

    override fun onStart() {
        super.onStart()

        val intent = intent
        val unitId = intent.getIntExtra("unit_id", 0)

        presenter.init(this, unitId)
        presenter.getUnit(unitId)
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

        val dateFormatter = SimpleDateFormat(getString(R.string.format_datetime), Locale.getDefault())

        title = unit.label
        labelEditText.setText(unit.label)
        stepEditText.setText(String.format("%.2f", unit.step))
        createdAtText.text = dateFormatter.format(unit.createdAt)
        updatedAtText.text = dateFormatter.format(unit.updatedAt)
    }

    override fun onLoading() {
        progressBar.visibility = View.VISIBLE
        fab.hide()
    }

    override fun onLoaded() {
        progressBar.visibility = View.GONE
        fab.show()
    }

    override fun showToast(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun showSnackbar(message: String?) {
        message ?: return

        Snackbar.make(labelEditText, message, Snackbar.LENGTH_LONG).show()
    }
}
