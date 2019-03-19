package com.refrii.client.food

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.text.Editable
import android.text.TextWatcher
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

class FoodActivity : AppCompatActivity(), FoodContract.View {

    private val mProgressBar: ProgressBar by bindView(R.id.foodProgressBar)
    private val mToolbar: Toolbar by bindView(R.id.toolbar)
    private val mName: EditText by bindView(R.id.nameEditText)
    private val mAmount: EditText by bindView(R.id.amountEditText)
    private val mNotice: EditText by bindView(R.id.noticeEditText)
    private val mExpirationDate: TextView by bindView(R.id.expirationDateTextView)
    private val mCreated: TextView by bindView(R.id.createdTextView)
    private val mUpdate: TextView by bindView(R.id.updatedTextView)
    private val mBoxName: TextView by bindView(R.id.boxTextView)
    private val mFab: FloatingActionButton by bindView(R.id.fab)
    private val mUnitsSpinner: Spinner by bindView(R.id.unitsSpinner)

    @Inject
    lateinit var mPresenter: FoodPresenter

    lateinit var mPreference: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (application as App).getComponent().inject(this)

        setContentView(R.layout.activity_food)
        setSupportActionBar(mToolbar)

        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeButtonEnabled(true)
        }

        mPreference = PreferenceManager.getDefaultSharedPreferences(this)

        mFab.setOnClickListener { mPresenter.updateFood() }
        mName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                mPresenter.updateName(s.toString())
            }
        })
        mNotice.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                mPresenter.updateNotice(s.toString())
            }
        })
        mAmount.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                mPresenter.updateAmount(s.toString().toDouble())
            }
        })
        mExpirationDate.setOnClickListener { mPresenter.editExpirationDate() }
    }

    override fun onStart() {
        super.onStart()

        val intent = intent
        val foodId = intent.getIntExtra(getString(R.string.key_food_id), 0)
        val boxId = intent.getIntExtra(getString(R.string.key_box_id), 0)
        val userId = mPreference.getInt(getString(R.string.preference_key_id), 0)

        mPresenter.takeView(this)
        mPresenter.getFood(foodId)
        mPresenter.getUnits(userId)
        hideProgressBar()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (data == null || resultCode != Activity.RESULT_OK) return

        when (requestCode) {
            EDIT_NAME_REQUEST_CODE -> mPresenter.updateName(data.getStringExtra("text"))
            EDIT_AMOUNT_REQUEST_CODE -> mPresenter.updateAmount(data.getDoubleExtra("number", 0.toDouble()))
            EDIT_NOTICE_REQUEST_CODE -> mPresenter.updateNotice(data.getStringExtra("text"))
            EDIT_EXPIRATION_DATE_REQUEST_CODE -> {
                val date = Date()

                date.time = data.getLongExtra("date", 0)
                mPresenter.updateExpirationDate(date)
            }
        }
    }

    override fun setFood(food: Food?) {
        val timeFormatter = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())

        mToolbar.title = food?.name
        setSupportActionBar(mToolbar)

        mName.setText(food?.name)
        mBoxName.text = food?.box?.name
        mAmount.setText(String.format("%.2f", food?.amount))
        mNotice.setText(food?.notice)
        mCreated.text = "${timeFormatter.format(food?.createdAt)} (${food?.createdUser?.name})"
        mUpdate.text = "${timeFormatter.format(food?.updatedAt)} (${food?.updatedUser?.name})"

        setExpirationDate(food?.expirationDate)
    }

    override fun setExpirationDate(date: Date?) {
        val today = Date()
        val oneDayMilliSec = 24 * 60 * 60 * 1000
        val daysLeft = ((date?.time ?: today.time) - today.time) / oneDayMilliSec
        val dateFormatter = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())

        mExpirationDate.text = dateFormatter.format(date)

        if (daysLeft < 0) {
            mExpirationDate.append(" (${Math.abs(daysLeft)} 日過ぎています)")
            mExpirationDate.setTextColor(Color.RED)
        } else {
            mExpirationDate.append(" (残り ${Math.abs(daysLeft)} 日)")
            mExpirationDate.setTextColor(Color.BLACK)
        }
    }

    override fun setUnits(units: List<Unit>?) {
        units ?: return

        val unitLabels = units.map { it.label }
        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, unitLabels)

        mUnitsSpinner.adapter = adapter
    }

    override fun showEditDateDialog(date: Date?) {
        date ?: return

        val fragment = CalendarPickerDialogFragment.newInstance(date)

        fragment.setTargetFragment(null, EDIT_EXPIRATION_DATE_REQUEST_CODE)
        fragment.show(supportFragmentManager, "edit_expiration_date")
    }

    override fun onUpdateCompleted() {
        finish()
    }

    override fun showProgressBar() {
        mProgressBar.visibility = View.VISIBLE
        mFab.hide()
    }

    override fun hideProgressBar() {
        mProgressBar.visibility = View.GONE
        mFab.show()
    }

    override fun showSnackbar(message: String?) {
        message ?: return

        Snackbar.make(mName, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun showToast(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    companion object {
        private const val EDIT_NAME_REQUEST_CODE = 100
        private const val EDIT_AMOUNT_REQUEST_CODE = 101
        private const val EDIT_NOTICE_REQUEST_CODE = 102
        private const val EDIT_EXPIRATION_DATE_REQUEST_CODE = 103
    }
}
