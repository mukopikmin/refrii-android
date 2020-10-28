package app.muko.mypantry.newunit

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import app.muko.mypantry.App
import app.muko.mypantry.R
import app.muko.mypantry.data.models.Unit
import butterknife.BindView
import butterknife.ButterKnife
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.android.AndroidInjection
import javax.inject.Inject

class NewUnitActivity : AppCompatActivity(), NewUnitContract.View {

    @BindView(R.id.toolbar)
    lateinit var toolbar: Toolbar

    @BindView(R.id.labelEditText)
    lateinit var labelEditText: EditText

    @BindView(R.id.stepEditText)
    lateinit var stepEditText: EditText

    @BindView(R.id.fab)
    lateinit var fab: FloatingActionButton

    @BindView(R.id.progressBar)
    lateinit var progressBar: ProgressBar

    @BindView(R.id.timestampContainer)
    lateinit var timestamp: View

    @Inject
    lateinit var presenter: NewUnitPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AndroidInjection.inject(this)
//        (application as App).getComponent().inject(this)

        setContentView(R.layout.activity_new_unit)
        ButterKnife.bind(this)
        hideProgressBar()
        setSupportActionBar(toolbar)
        toolbar.title = getString(R.string.title_add_unit)
        timestamp.visibility = View.GONE
        setSupportActionBar(toolbar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeButtonEnabled(true)
        }

        fab.setOnClickListener {
            val label = labelEditText.text.toString()
            val step = stepEditText.text.toString().toDouble()
            val unit = Unit.temp(label, step)

            presenter.createUnit(unit)
        }
    }

    override fun onStart() {
        super.onStart()

        presenter.init(this)
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

    override fun onCreateCompleted(unit: Unit?) {
        unit ?: return

        val intent = Intent()

        intent.putExtra(getString(R.string.key_unit_id), unit.id)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    override fun showProgressBar() {
        progressBar.visibility = View.VISIBLE
    }

    override fun hideProgressBar() {
        progressBar.visibility = View.GONE
    }

    override fun showToast(message: String?) {
        message ?: return
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
