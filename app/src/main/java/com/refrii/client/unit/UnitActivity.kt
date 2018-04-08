package com.refrii.client.unit

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.refrii.client.App
import com.refrii.client.R
import com.refrii.client.data.api.models.Unit
import com.refrii.client.dialogs.EditDoubleDialogFragment
import com.refrii.client.dialogs.EditTextDialogFragment
import kotterknife.bindView
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class UnitActivity : AppCompatActivity(), UnitContract.View {

    private val mToolbar: Toolbar by bindView(R.id.toolbar)
    private val mLabel: TextView by bindView(R.id.labelTextView)
    private val mStep: TextView by bindView(R.id.stepTextView)
    private val mCreated: TextView by bindView(R.id.createdTextView)
    private val mUpdated: TextView by bindView(R.id.updatedTextView)
    private val mEditLabel: ImageView by bindView(R.id.labelImageView)
    private val mEditStep: ImageView by bindView(R.id.stepImageView)
    private val mFab: FloatingActionButton by bindView(R.id.fab)
    private val mEditedMessage: TextView by bindView(R.id.editedMessageTextView)
    private val mProgressBar: ProgressBar by bindView(R.id.progressBar)

    @Inject
    lateinit var mPresenter: UnitPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (application as App).getComponent().inject(this)

        setContentView(R.layout.activity_unit)
        setSupportActionBar(mToolbar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeButtonEnabled(true)
        }

        mEditLabel.setOnClickListener { mPresenter.editLabel() }
        mEditStep.setOnClickListener { mPresenter.editStep() }
        mFab.setOnClickListener { mPresenter.updateUnit() }
    }

    override fun onStart() {
        super.onStart()

        val intent = intent
        val unitId = intent.getIntExtra("unit_id", 0)

        mPresenter.takeView(this)
        mPresenter.getUnit(unitId)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) return
        data ?: return

        when (requestCode) {
            EDIT_LABEL_REQUEST_CODE -> {
                mPresenter.updateLabel(data.getStringExtra("text"))
                onEdited()
            }
            EDIT_STEP_REQUEST_CODE -> {
                mPresenter.updateStep(data.getDoubleExtra("number", 0.toDouble()))
                onEdited()
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

    override fun setUnit(unit: Unit?) {
        unit ?: return

        val formatter = SimpleDateFormat("yyyy/MM/dd HH/mm", Locale.getDefault())

        mLabel.text = unit.label
        mStep.text = unit.step.toString()
        mCreated.text = formatter.format(unit.createdAt)
        mUpdated.text = formatter.format(unit.updatedAt)
    }

    override fun onLoading() {
        mProgressBar.visibility = View.VISIBLE
    }

    override fun onLoaded() {
        mProgressBar.visibility = View.GONE
    }

    override fun onBeforeEdit() {
        mEditedMessage.visibility = View.GONE
        mFab.hide()
    }

    override fun onEdited() {
        mEditedMessage.visibility = View.VISIBLE
        mFab.show()
    }

    override fun showEditLabelDialog(label: String?) {
        label ?: return

        val fragment = EditTextDialogFragment.newInstance("Label", label)

        fragment.setTargetFragment(null, EDIT_LABEL_REQUEST_CODE)
        fragment.show(fragmentManager, "edit_label")
    }

    override fun showEditStepDIalog(step: Double?) {
        step ?: return

        val fragment = EditDoubleDialogFragment.newInstance("Step size", step)

        fragment.setTargetFragment(null, EDIT_STEP_REQUEST_CODE)
        fragment.show(fragmentManager, "edit_step")
    }

    override fun showToast(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun showSnackbar(message: String?) {
        message ?: return

        Snackbar.make(mLabel, message, Snackbar.LENGTH_LONG).show()
    }

    companion object {
        private const val EDIT_LABEL_REQUEST_CODE = 101
        private const val EDIT_STEP_REQUEST_CODE = 121
    }
}
