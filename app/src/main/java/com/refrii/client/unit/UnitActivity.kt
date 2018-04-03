package com.refrii.client.unit

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.widget.ImageView
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

    private val toolbar: Toolbar by bindView(R.id.toolbar)
    private val labelTextView: TextView by bindView(R.id.labelTextView)
    private val stepTextView: TextView by bindView(R.id.stepTextView)
    private val createdTextView: TextView by bindView(R.id.createdTextView)
    private val updatedTextView: TextView by bindView(R.id.updatedTextView)
    private val labelImageView: ImageView by bindView(R.id.labelImageView)
    private val stepImageView: ImageView by bindView(R.id.stepImageView)
    private val fab: FloatingActionButton by bindView(R.id.fab)

    @Inject
    lateinit var mPresenter: UnitPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (application as App).getComponent().inject(this)

        setContentView(R.layout.activity_unit)
        setSupportActionBar(toolbar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeButtonEnabled(true)
        }

        labelImageView.setOnClickListener { mPresenter.editLabel() }
        stepImageView.setOnClickListener { mPresenter.editStep() }
        fab.setOnClickListener { mPresenter.updateUnit() }
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

        labelTextView.text = unit.label
        stepTextView.text = unit.step.toString()
        createdTextView.text = formatter.format(unit.createdAt)
        updatedTextView.text = formatter.format(unit.updatedAt)
    }

    override fun onLoading() {
        fab.hide()
    }

    override fun onLoaded() {
        fab.show()
    }

    override fun onBeforeEdit() {

    }

    override fun onEdited() {
        fab.show()
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

    companion object {
        private const val TAG = "UnitActivity"
        private const val EDIT_LABEL_REQUEST_CODE = 101
        private const val EDIT_STEP_REQUEST_CODE = 121
    }
}
