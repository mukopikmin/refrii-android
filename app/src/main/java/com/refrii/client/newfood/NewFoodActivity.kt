package com.refrii.client.newfood

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
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

    private val toolbar: Toolbar by bindView(R.id.toolbar)
    private val nameEditText: EditText by bindView(R.id.newFoodNameEditText)
    private val noticeEditText: EditText by bindView(R.id.newFoodNoticeEditText)
    private val amountEditText: EditText by bindView(R.id.newFoodAmountEditText)
    private val spinner: Spinner by bindView(R.id.newFoodUnitSpinner)
    private val fab: FloatingActionButton by bindView(R.id.fab)
    private val expirationDateEditText: EditText by bindView(R.id.newFoodExpirationDateEditText)

    @Inject
    lateinit var mPresenter: NewFoodPresenter

    private val formatter = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (application as App).getComponent().inject(this)

        setContentView(R.layout.activity_new_food)
        toolbar.title = "Add food"
        setSupportActionBar(toolbar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeButtonEnabled(true)
        }

        expirationDateEditText.setText(formatter.format(Date()))

        fab.setOnClickListener { createFood() }
        expirationDateEditText.setOnClickListener { showEditDateDialog() }
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

                expirationDateEditText.setText(formatter.format(date))
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

        spinner.adapter = ArrayAdapter<String>(this@NewFoodActivity, android.R.layout.simple_spinner_dropdown_item, units.map { it.label })
    }

    private fun isCreatable(name: String): Boolean {
        return !name.isEmpty()
    }

    override fun creteCompleted(food: Food?) {
        food ?: return

        val intent = Intent()

        intent.putExtra("food_id", food.id)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    override fun showProgressBar() {

    }

    override fun hideProgressBar() {
    }

    override fun showToast(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun createFood() {
        val name = nameEditText.text.toString()
        val notice = noticeEditText.text.toString()
        val amount = amountEditText.text.toString().toDouble()
        val unit = mPresenter.pickUnit(spinner.selectedItem.toString())
        val expirationDate = formatter.parse(expirationDateEditText.text.toString())

        mPresenter.createFood(name, notice, amount, unit, expirationDate)
    }

    private fun showEditDateDialog() {
        val expirationDate = formatter.parse(expirationDateEditText.text.toString())
        val fragment = CalendarPickerDialogFragment.newInstance(expirationDate)

        fragment.setTargetFragment(null, EDIT_EXPIRATION_DATE_REQUEST_CODE)
        fragment.show(fragmentManager, "edit_expiration_date")
    }

    companion object {
        private const val TAG = "NewFoodActivity"
        private const val EDIT_EXPIRATION_DATE_REQUEST_CODE = 101
    }

}
