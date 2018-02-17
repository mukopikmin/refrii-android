package com.refrii.client.views.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import com.refrii.client.R
import com.refrii.client.RealmUtil
import com.refrii.client.models.Box
import com.refrii.client.models.Food
import com.refrii.client.models.Unit
import com.refrii.client.services.FoodService
import com.refrii.client.services.RetrofitFactory
import com.refrii.client.services.UnitService
import com.refrii.client.views.fragments.CalendarPickerDialogFragment
import io.realm.Realm
import kotterknife.bindView
import okhttp3.MultipartBody
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*

class NewFoodActivity : AppCompatActivity() {

    private val toolbar: Toolbar by bindView(R.id.toolbar)
    private val nameEditText: EditText by bindView(R.id.newFoodNameEditText)
    private val noticeEditText: EditText by bindView(R.id.newFoodNoticeEditText)
    private val amountEditText: EditText by bindView(R.id.newFoodAmountEditText)
    private val spinner: Spinner by bindView(R.id.newFoodUnitSpinner)
    private val fab: FloatingActionButton by bindView(R.id.fab)
    private val expirationDateEditText: EditText by bindView(R.id.newFoodExpirationDateEditText)

    private lateinit var mRealm: Realm
    private var mUnits: List<Unit>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_new_food)
        toolbar.title = "Add food"
        setSupportActionBar(toolbar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeButtonEnabled(true)
        }

        Realm.init(this)
        mRealm = RealmUtil.getInstance()

        val intent = intent
        val boxId = intent.getIntExtra("boxId", 0)
        val date = Date()
        val formatter = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())

        mUnits = getUnits()
        expirationDateEditText.setText(formatter.format(date))

        fab.setOnClickListener {
            val name = nameEditText.text.toString()
            val notice = noticeEditText.text.toString()
            val amount = amountEditText.text.toString().toDouble()
            val unitLabel = spinner.selectedItem.toString()
            val box = mRealm.where(Box::class.java).equalTo("id", boxId).findFirst()
            val unit = mUnits?.firstOrNull { it.label == unitLabel }
            val expirationDate = formatter.parse(expirationDateEditText.text.toString())

            unit?.let {
                if (isCreatable(name)) addFood(name, notice, amount, box, unit, expirationDate)
            }
        }

        expirationDateEditText.setOnClickListener {
            val expirationDate = formatter.parse(expirationDateEditText.text.toString())
            val fragment = CalendarPickerDialogFragment.newInstance(expirationDate)

            fragment.setTargetFragment(null, EDIT_EXPIRATION_DATE_REQUEST_CODE)
            fragment.show(fragmentManager, "edit_expiration_date")
        }
    }

    override fun onStart() {
        super.onStart()

        syncUnits()
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

    private fun getUnits(): List<Unit>? = mRealm.where(Unit::class.java)?.findAll()

    private fun syncUnits() {
        RetrofitFactory.getClient(UnitService::class.java, this@NewFoodActivity)
                .getUnits()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object: Subscriber<List<Unit>>() {
                    override fun onCompleted() {
                        Log.d(TAG, "Completed syncing food.")
                    }

                    override fun onError(e: Throwable) {
                        Toast.makeText(this@NewFoodActivity, e.message, Toast.LENGTH_LONG).show()
                    }

                    override fun onNext(t: List<Unit>) {
                        mUnits = t
                        mRealm.executeTransaction {
                            t.forEach { mRealm.copyToRealmOrUpdate(it) }
                        }
                        spinner.adapter = ArrayAdapter<String>(this@NewFoodActivity, android.R.layout.simple_spinner_dropdown_item, t.map { it.label })
                    }
                })
    }

    private fun addFood(name: String, notice: String, amount: Double, box: Box, unit: Unit, expirationDate: Date) {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val body = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("name", name)
                .addFormDataPart("notice", notice)
                .addFormDataPart("amount", amount.toString())
                .addFormDataPart("box_id", box.id.toString())
                .addFormDataPart("unit_id", unit.id.toString())
                .addFormDataPart("expiration_date", simpleDateFormat.format(expirationDate))
                .build()

        RetrofitFactory.getClient(FoodService::class.java, this)
                .addFood(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object: Subscriber<Food>() {
                    override fun onCompleted() {
                        showProgressBar()
                    }

                    override fun onNext(t: Food) {
                        mRealm.executeTransaction { mRealm.copyToRealmOrUpdate(t) }

                        val intent = Intent()
                        intent.putExtra("food_id", t.id)
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                    }

                    override fun onError(e: Throwable) {
                        Toast.makeText(this@NewFoodActivity, e.message, Toast.LENGTH_LONG).show()
                    }
                })
    }

    private fun isCreatable(name: String): Boolean {
        return !name.isEmpty()
    }

    private fun showProgressBar() {

    }

    companion object {
        private const val TAG = "NewFoodActivity"
        private const val EDIT_EXPIRATION_DATE_REQUEST_CODE = 101
    }

}
