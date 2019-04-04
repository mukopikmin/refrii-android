package com.refrii.client.newfood

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.refrii.client.App
import com.refrii.client.R
import com.refrii.client.data.api.models.Box
import com.refrii.client.data.api.models.Food
import com.refrii.client.data.api.models.Unit
import com.refrii.client.dialogs.CalendarPickerDialogFragment
import kotterknife.bindView
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class NewFoodActivity : AppCompatActivity(), NewFoodContract.View {

    private val mToolbar: Toolbar by bindView(R.id.toolbar)
    private val mNameEditText: EditText by bindView(R.id.nameEditText)
    private val mBoxNameText: TextView by bindView(R.id.boxTextView)
    private val mNoticeEditText: EditText by bindView(R.id.noticeEditText)
    private val mAmountEditText: EditText by bindView(R.id.amountEditText)
    private val mSpinner: Spinner by bindView(R.id.unitsSpinner)
    private val mFab: FloatingActionButton by bindView(R.id.fab)
    private val mExpirationDateEditText: TextView by bindView(R.id.expirationDateTextView)
    private val mProgressBar: ProgressBar by bindView(R.id.progressBar)
    private val mHistoryContainer: ConstraintLayout by bindView(R.id.historyContainer)

    @Inject
    lateinit var mPresenter: NewFoodPresenter

    private val formatter = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (application as App).getComponent().inject(this)

        setContentView(R.layout.activity_new_food)
        mToolbar.title = getString(R.string.title_add_food)
        setSupportActionBar(mToolbar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeButtonEnabled(true)
        }

        mExpirationDateEditText.text = formatter.format(Date())
        mFab.setOnClickListener { createFood() }
        mExpirationDateEditText.setOnClickListener { showEditDateDialog() }

        mHistoryContainer.visibility = View.GONE
        hideProgressBar()
    }

    override fun onStart() {
        super.onStart()

        val intent = intent
        val boxId = intent.getIntExtra(getString(R.string.key_box_id), 0)

        mPresenter.takeView(this)
        mPresenter.getUnits(boxId)
        mPresenter.getBox(boxId)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK || data == null) return

        when (requestCode) {
            EDIT_EXPIRATION_DATE_REQUEST_CODE -> {
                val formatter = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
                val date = Date(data.getLongExtra("date", 0.toLong()))

                mExpirationDateEditText.setText(formatter.format(date))
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

    override fun setBox(box: Box?) {
        box ?: return

        mBoxNameText.text = box.name
    }

    override fun setUnits(units: List<Unit>?) {
        units ?: return

        val unitLabels = units.map { it.label }
        val adapter = ArrayAdapter<String>(this@NewFoodActivity, android.R.layout.simple_spinner_dropdown_item, unitLabels)

        mSpinner.adapter = adapter
    }

    override fun createCompleted(food: Food?) {
        food ?: return

        val intent = Intent()

        intent.putExtra(getString(R.string.key_food_id), food.id)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    override fun showProgressBar() {
        mFab.hide()
        mProgressBar.visibility = View.VISIBLE
    }

    override fun hideProgressBar() {
        mFab.show()
        mProgressBar.visibility = View.GONE
    }

    override fun showToast(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun validate(): Boolean {
        if (mNameEditText.text.isBlank()) {
            mNameEditText.error = "Name is empty"
        }

        if (mAmountEditText.text.isBlank()) {
            mAmountEditText.error = "Amount is empty"
        }

        return mNameEditText.text.isNotBlank() && mAmountEditText.text.isNotBlank()
    }

    private fun createFood() {
        if (!validate()) {
            showToast(getString(R.string.message_unfilled_form))
            return
        }

        val name = mNameEditText.text.toString()
        val notice = mNoticeEditText.text.toString()
        val amount = mAmountEditText.text.toString().toDouble()
        val unit = mPresenter.pickUnit(mSpinner.selectedItem.toString())
        val expirationDate = formatter.parse(mExpirationDateEditText.text.toString())

        mPresenter.createFood(name, notice, amount, unit, expirationDate)
    }

    private fun showEditDateDialog() {
        val expirationDate = formatter.parse(mExpirationDateEditText.text.toString())
        val fragment = CalendarPickerDialogFragment.newInstance(expirationDate)

        fragment.setTargetFragment(null, EDIT_EXPIRATION_DATE_REQUEST_CODE)
        fragment.show(supportFragmentManager, "edit_expiration_date")
    }

    companion object {
        private const val EDIT_EXPIRATION_DATE_REQUEST_CODE = 101
    }

}
