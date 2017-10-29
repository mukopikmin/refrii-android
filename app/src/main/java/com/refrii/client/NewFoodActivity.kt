package com.refrii.client

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast

import java.text.SimpleDateFormat
import java.util.Date

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NewFoodActivity : AppCompatActivity() {

    private var units: List<Unit>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_food)
        val toolbar = findViewById<Toolbar>(R.id.toolbar) as Toolbar
        toolbar.title = "Register food"
        setSupportActionBar(toolbar)

        val intent = intent
        val boxId = intent.getIntExtra("boxId", 0)

        val spinner = findViewById<Spinner>(R.id.newFoodUnitSpinner) as Spinner

        val fab = findViewById<FloatingActionButton>(R.id.fab) as FloatingActionButton
        fab.setOnClickListener {
            val nameEditText = findViewById<EditText>(R.id.newFoodNameEditText) as EditText
            val noticeEditText = findViewById<EditText>(R.id.newFoodNoticeEditText) as EditText
            val amountEditText = findViewById<EditText>(R.id.newFoodAmountEditText) as EditText

            val selectedUnitLabel = spinner!!.selectedItem.toString()
            var selectedUnit: Unit? = null
            for (unit in units!!) {
                if (unit.label == selectedUnitLabel) {
                    selectedUnit = unit
                    break
                }
            }

            val expirationDate = Date()
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")

            val body = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("name", nameEditText.text.toString())
                    .addFormDataPart("notice", noticeEditText.text.toString())
                    .addFormDataPart("amount", amountEditText.text.toString())
                    .addFormDataPart("box_id", boxId.toString())
                    .addFormDataPart("unit_id", selectedUnit!!.id.toString())
                    .addFormDataPart("expiration_date", simpleDateFormat.format(expirationDate))
                    .build()

            val service = RetrofitFactory.getClient(FoodService::class.java, this@NewFoodActivity)
            val call = service.addFood(body)
            call.enqueue(object : BasicCallback<Food>(this@NewFoodActivity) {
                override fun onResponse(call: Call<Food>, response: Response<Food>) {
                    if (response.code() == 201) {
                        // With success of creating food, exit this activity
                        val food = response.body()
                        val intent = Intent()
                        intent.putExtra("food", food)
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                    } else {
                        Log.d(TAG, "Failed with status: " + response.code())
                    }
                }

                override fun onFailure(call: Call<Food>, t: Throwable) {
                    Log.d(TAG, t.message)
                    Toast.makeText(this@NewFoodActivity, t.message, Toast.LENGTH_LONG).show()
                }
            })
        }

        val service = RetrofitFactory.getClient(UnitService::class.java, this@NewFoodActivity)
        val call = service.units
        call.enqueue(object : BasicCallback<MutableList<Unit>>(this@NewFoodActivity) {
            override fun onResponse(call: Call<MutableList<Unit>>, response: Response<MutableList<Unit>>) {
                super.onResponse(call, response)

                units = response.body()
                val adapter = ArrayAdapter<String>(this@NewFoodActivity, android.R.layout.simple_spinner_dropdown_item)
                units!!.forEach { adapter.add(it.label) }
                spinner.adapter = adapter
            }

            override fun onFailure(call: Call<MutableList<Unit>>, t: Throwable) {
                super.onFailure(call, t)
                Toast.makeText(this@NewFoodActivity, t.message, Toast.LENGTH_LONG).show()
            }
        })
    }

    companion object {
        private val TAG = "NewFoodActivity"
    }

}
