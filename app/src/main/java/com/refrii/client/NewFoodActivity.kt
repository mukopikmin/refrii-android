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

    private var sharedPreferences: SharedPreferences? = null
    private var spinner: Spinner? = null

    private var units: List<Unit>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_food)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        toolbar.title = "Register food"
        setSupportActionBar(toolbar)

        val intent = intent
        val boxId = intent.getIntExtra("boxId", 0)

        spinner = findViewById(R.id.newFoodUnitSpinner) as Spinner

        sharedPreferences = getSharedPreferences("DATA", Context.MODE_PRIVATE)
        val token = sharedPreferences!!.getString("jwt", null)

        val fab = findViewById(R.id.fab) as FloatingActionButton
        fab.setOnClickListener {
            val nameEditText = findViewById(R.id.newFoodNameEditText) as EditText
            val noticeEditText = findViewById(R.id.newFoodNoticeEditText) as EditText
            val amountEditText = findViewById(R.id.newFoodAmountEditText) as EditText

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
        call.enqueue(object : Callback<List<Unit>> {
            override fun onResponse(call: Call<List<Unit>>, response: Response<List<Unit>>) {
                units = response.body()
                val adapter = ArrayAdapter<String>(this@NewFoodActivity, android.R.layout.simple_spinner_dropdown_item)
                for (unit in units!!) {
                    adapter.add(unit.label)
                }
                spinner!!.adapter = adapter
            }

            override fun onFailure(call: Call<List<Unit>>, t: Throwable) {
                Toast.makeText(this@NewFoodActivity, t.message, Toast.LENGTH_LONG).show()
            }
        })
    }

    companion object {

        private val TAG = "NewFoodActivity"
    }

}
