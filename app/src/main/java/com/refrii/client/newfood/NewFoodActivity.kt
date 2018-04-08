package com.refrii.client.newfood

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.refrii.client.App
import com.refrii.client.R
import com.refrii.client.data.api.models.Food
import com.refrii.client.data.api.models.Unit
import com.refrii.client.dialogs.CalendarPickerDialogFragment
import kotterknife.bindView
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class NewFoodActivity : AppCompatActivity(), NewFoodContract.View {

    private val mToolbar: Toolbar by bindView(R.id.toolbar)
    private val mNameEditText: EditText by bindView(R.id.newFoodNameEditText)
    private val mNoticeEditText: EditText by bindView(R.id.newFoodNoticeEditText)
    private val mAmountEditText: EditText by bindView(R.id.newFoodAmountEditText)
    private val mSpinner: Spinner by bindView(R.id.newFoodUnitSpinner)
    private val mFab: FloatingActionButton by bindView(R.id.fab)
    private val mExpirationDateEditText: EditText by bindView(R.id.newFoodExpirationDateEditText)
    private val mProgressBar: ProgressBar by bindView(R.id.progressBar)

    @Inject
    lateinit var mPresenter: NewFoodPresenter

    private val formatter = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (application as App).getComponent().inject(this)

        setContentView(R.layout.activity_new_food)
        mToolbar.title = "Add food"
        setSupportActionBar(mToolbar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeButtonEnabled(true)
        }

        mExpirationDateEditText.setText(formatter.format(Date()))

        mFab.setOnClickListener { createFood() }
        mExpirationDateEditText.setOnClickListener { showEditDateDialog() }
    }

    override fun onStart() {
        super.onStart()

        val preference = PreferenceManager.getDefaultSharedPreferences(this)
        val userId = preference.getInt("id", 0)
        val intent = intent
        val boxId = intent.getIntExtra("boxId", 0)

        mPresenter.takeView(this)
        mPresenter.getUnits(userId)
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

    override fun setUnits(units: List<Unit>?) {
        units ?: return

        val unitLabels = units.map { it.label }
        val adapter = ArrayAdapter<String>(this@NewFoodActivity, android.R.layout.simple_spinner_dropdown_item, unitLabels)

        mSpinner.adapter = adapter
    }

    override fun createCompleted(food: Food?) {
        food ?: return

        val intent = Intent()

        intent.putExtra("food_id", food.id)
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

    private fun isNotCreatable(): Boolean {
        return mNameEditText.text.isBlank()
                || mExpirationDateEditText.text.isBlank()
    }

    private fun createFood() {
        if (isNotCreatable()) {
            showToast("Forms are not filled")
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
        fragment.show(fragmentManager, "edit_expiration_date")
    }

    companion object {
        private const val EDIT_EXPIRATION_DATE_REQUEST_CODE = 101
    }

}
