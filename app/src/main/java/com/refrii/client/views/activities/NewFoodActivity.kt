package com.refrii.client.views.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import com.refrii.client.BasicCallback
import com.refrii.client.R
import com.refrii.client.RetrofitFactory
import com.refrii.client.models.Box
import com.refrii.client.services.UnitService
import com.refrii.client.models.Food
import com.refrii.client.models.Unit
import com.refrii.client.services.FoodService
import io.realm.Realm
import io.realm.RealmConfiguration
import kotterknife.bindView

import java.text.SimpleDateFormat

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Response
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.*

class NewFoodActivity : AppCompatActivity() {

    private val toolbar: Toolbar by bindView(R.id.toolbar)
    private val nameEditText: EditText by bindView(R.id.newFoodNameEditText)
    private val noticeEditText: EditText by bindView(R.id.newFoodNoticeEditText)
    private val amountEditText: EditText by bindView(R.id.newFoodAmountEditText)
    private val spinner: Spinner by bindView(R.id.newFoodUnitSpinner)
    private val fab: FloatingActionButton by bindView(R.id.fab)

    private var mRealm: Realm? = null
    private var mUnits: List<Unit>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_food)

        Realm.setDefaultConfiguration(RealmConfiguration.Builder(this).build())
        mRealm = Realm.getDefaultInstance()

        toolbar.title = "Add food"
        setSupportActionBar(toolbar)

        val intent = intent
        val boxId = intent.getIntExtra("boxId", 0)

        mUnits = getUnits()

        fab.setOnClickListener {
            val name = nameEditText.text.toString()
            val notice = noticeEditText.text.toString()
            val amount = amountEditText.text.toString().toDouble()
            val unitLabel = spinner.selectedItem.toString()
            val unit: Unit? = mUnits!!.firstOrNull { it.label == unitLabel }
            val expirationDate = Date()

            if (unit?.id != null) {
                addFood(name, notice, amount, boxId, unit.id, expirationDate)
            }
        }
    }

    override fun onStart() {
        super.onStart()

        syncUnits()
    }

    private fun getUnits(): List<Unit>? = mRealm?.where(Unit::class.java)?.findAll()

    private fun syncUnits() {
        val service = RetrofitFactory.getClient(UnitService::class.java, this@NewFoodActivity)
        val call = service.units
        call.enqueue(object : BasicCallback<MutableList<Unit>>(this@NewFoodActivity) {
            override fun onResponse(call: Call<MutableList<Unit>>, response: Response<MutableList<Unit>>) {
                super.onResponse(call, response)

                mUnits = response.body()
                val adapter = ArrayAdapter<String>(this@NewFoodActivity, android.R.layout.simple_spinner_dropdown_item)
                mUnits!!.forEach { adapter.add(it.label) }
                spinner.adapter = adapter
            }

            override fun onFailure(call: Call<MutableList<Unit>>, t: Throwable) {
                super.onFailure(call, t)
                Toast.makeText(this@NewFoodActivity, t.message, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun addFood(name: String, notice: String, amount: Double, boxId: Int, unitId: Int, expirationDate: Date) {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val body = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("name", name)
                .addFormDataPart("notice", notice)
                .addFormDataPart("amount", amount.toString())
                .addFormDataPart("box_id", boxId.toString())
                .addFormDataPart("unit_id", unitId.toString())
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
                        mRealm?.let { realm ->
                            realm.executeTransaction {
                                realm.copyToRealmOrUpdate(t)
                            }
                        }

                        val intent = Intent()
                        intent.putExtra("food_id", t.id)
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                    }

                    override fun onError(e: Throwable?) {
                        e ?: return

                        Toast.makeText(this@NewFoodActivity, e.message, Toast.LENGTH_LONG).show()
                    }

                })
    }

    private fun showProgressBar() {

    }

    companion object {
        private val TAG = "NewFoodActivity"
    }

}
