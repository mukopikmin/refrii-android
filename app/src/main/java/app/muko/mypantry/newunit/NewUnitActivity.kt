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
import javax.inject.Inject

class NewUnitActivity : AppCompatActivity(), NewUnitContract.View {

    @BindView(R.id.toolbar)
    lateinit var mToolbar: Toolbar
    @BindView(R.id.labelEditText)
    lateinit var mLabelEditText: EditText
    @BindView(R.id.stepEditText)
    lateinit var mStepEditText: EditText
    @BindView(R.id.fab)
    lateinit var mFab: FloatingActionButton
    @BindView(R.id.progressBar)
    lateinit var mProgressBar: ProgressBar
    @BindView(R.id.timestampContainer)
    lateinit var mTimestamp: View

    @Inject
    lateinit var mPresenter: NewUnitContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (application as App).getComponent().inject(this)

        setContentView(R.layout.activity_new_unit)
        ButterKnife.bind(this)
        hideProgressBar()
        setSupportActionBar(mToolbar)
        mToolbar.title = getString(R.string.title_add_unit)
        mTimestamp.visibility = View.GONE
        setSupportActionBar(mToolbar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeButtonEnabled(true)
        }

        mFab.setOnClickListener {
            val label = mLabelEditText.text.toString()
            val step = mStepEditText.text.toString().toDouble()

            mPresenter.createUnit(label, step)
        }
    }

    override fun onStart() {
        super.onStart()

        mPresenter.takeView(this)
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
        mProgressBar.visibility = View.VISIBLE
    }

    override fun hideProgressBar() {
        mProgressBar.visibility = View.GONE
    }

    override fun showToast(message: String?) {
        message ?: return
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
