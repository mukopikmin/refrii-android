package com.refrii.client

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.View
import android.widget.EditText
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Response

class NewUnitActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_unit)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        val labelEditText = findViewById(R.id.labelEditText) as EditText
        val fab = findViewById(R.id.fab) as FloatingActionButton

        fab.setOnClickListener { view ->
            createUnit(labelEditText.text.toString())
        }
    }

    private fun createUnit(label: String) {
        val service = RetrofitFactory.getClient(UnitService::class.java, this@NewUnitActivity)
        val body = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("label", label)
                .addFormDataPart("step", "1")
                .build()
        service.createUnit(body).enqueue(object: BasicCallback<Unit>(this@NewUnitActivity) {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                super.onResponse(call, response)

                if (response.code() == 201) {
                    val unit = response.body() as Unit
                    val intent = Intent()

                    intent.putExtra("unit", unit)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                } else {
                    Log.d(TAG, "Failed with status: " + response.code())
                }
            }
        })
    }

    companion object {
        private val TAG = "NewUnitActivity"
    }
}
