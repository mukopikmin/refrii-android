package com.refrii.client.food

import android.app.Activity
import android.content.Intent
import android.graphics.Color
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
import com.refrii.client.data.api.models.Box
import com.refrii.client.data.api.models.Food
import com.refrii.client.dialogs.CalendarPickerDialogFragment
import com.refrii.client.dialogs.EditDoubleDialogFragment
import com.refrii.client.dialogs.EditTextDialogFragment
import kotterknife.bindView
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class FoodActivity : AppCompatActivity(), FoodContract.View {

    private val mProgressBar: ProgressBar by bindView(R.id.foodProgressBar)
    private val mEditedMessage: TextView by bindView(R.id.editedMessageTextView)
    private val mEditName: ImageView by bindView(R.id.editNameImageView)
    private val mEditAmount: ImageView by bindView(R.id.editAmountImageView)
    private val mEditNotice: ImageView by bindView(R.id.editNoticeImageView)
    private val mEditExpirationDate: ImageView by bindView(R.id.editExpirationDateImageView)
    private val mToolbar: Toolbar by bindView(R.id.toolbar)
    private val mName: TextView by bindView(R.id.foodNameTextView)
    private val mAmount: TextView by bindView(R.id.amountTextView)
    private val mNotice: TextView by bindView(R.id.noticeTextView)
    private val mExpirationDate: TextView by bindView(R.id.expirationDateTextView)
    private val mCreated: TextView by bindView(R.id.createdUserTextView)
    private val mUpdate: TextView by bindView(R.id.updatedUserTextView)
    private val mBoxName: TextView by bindView(R.id.foodBoxTextView)
    private val mFab: FloatingActionButton by bindView(R.id.fab)

    @Inject
    lateinit var mPresenter: FoodPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (application as App).getComponent().inject(this)

        setContentView(R.layout.activity_food)
        setSupportActionBar(mToolbar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeButtonEnabled(true)
        }

        mFab.setOnClickListener { mPresenter.updateFood() }
        mEditName.setOnClickListener { mPresenter.editName() }
        mEditAmount.setOnClickListener { mPresenter.editAmount() }
        mEditNotice.setOnClickListener { mPresenter.editNotice() }
        mEditExpirationDate.setOnClickListener { mPresenter.editExpirationDate() }
    }

    override fun onStart() {
        super.onStart()

        val intent = intent
        val foodId = intent.getIntExtra(getString(R.string.key_food_id), 0)
        val boxId = intent.getIntExtra(getString(R.string.key_box_id), 0)

        mPresenter.takeView(this)
        mPresenter.getFood(foodId)
        mPresenter.getBox(boxId)

        onBeforeEdit()
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

    override fun setFood(food: Food?, box: Box?) {
        val timeFormatter = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())
        val dateFormatter = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
        val daysLeft = food?.daysLeft()

        mToolbar.title = food?.name
        setSupportActionBar(mToolbar)

        mName.text = food?.name
        mBoxName.text = box?.name
        mAmount.text = "${food?.amount} ${food?.unit?.label}"
        mNotice.text = food?.notice
        mCreated.text = "${timeFormatter.format(food?.createdAt)} by ${food?.createdUser?.name}"
        mUpdate.text = "${timeFormatter.format(food?.updatedAt)} by ${food?.updatedUser?.name}"
        mExpirationDate.text = dateFormatter.format(food?.expirationDate)

        daysLeft?.let {
            if (daysLeft < 0) {
                mExpirationDate.append(" (${Math.abs(daysLeft)} days over)")
                mExpirationDate.setTextColor(Color.RED)
            } else {
                mExpirationDate.append(" (${Math.abs(daysLeft)} days left)")
            }
        }
    }

    override fun showEditNameDialog(name: String?) {
        name ?: return

        val fragment = EditTextDialogFragment.newInstance("Name", name)

        fragment.setTargetFragment(null, EDIT_NAME_REQUEST_CODE)
        fragment.show(fragmentManager, "edit_name")
    }

    override fun showEditAmountDialog(amount: Double?) {
        amount ?: return

        val fragment = EditDoubleDialogFragment.newInstance("Amount", amount)

        fragment.setTargetFragment(null, EDIT_AMOUNT_REQUEST_CODE)
        fragment.show(fragmentManager, "edit_amount")
    }

    override fun showEditNoticeDialog(notice: String?) {
        notice ?: return

        val fragment = EditTextDialogFragment.newInstance("Notice", notice, true)

        fragment.setTargetFragment(null, EDIT_NOTICE_REQUEST_CODE)
        fragment.show(fragmentManager, "edit_notice")
    }

    override fun showEditDateDialog(date: Date?) {
        date ?: return

        val fragment = CalendarPickerDialogFragment.newInstance(date)

        fragment.setTargetFragment(null, EDIT_EXPIRATION_DATE_REQUEST_CODE)
        fragment.show(supportFragmentManager, "edit_expiration_date")
    }

    override fun onBeforeEdit() {
        mEditedMessage.visibility = View.GONE
        mFab.hide()
    }

    override fun onEdited() {
        mEditedMessage.visibility = View.VISIBLE
        mFab.show()
    }

    override fun showProgressBar() {
        mProgressBar.visibility = View.VISIBLE
    }

    override fun hideProgressBar() {
        mProgressBar.visibility = View.GONE
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
