package com.refrii.client.views.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.widget.EditText
import android.widget.Toast
import com.refrii.client.R
import com.refrii.client.factories.RetrofitFactory
import com.refrii.client.models.Unit
import com.refrii.client.services.UnitService
import kotterknife.bindView
import okhttp3.MultipartBody
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class NewUnitActivity : AppCompatActivity() {

    private val toolbar: Toolbar by bindView(R.id.toolbar)
    private val labelEditText: EditText by bindView(R.id.labelEditText)
    private val fab: EditText by bindView(R.id.fab)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_new_unit)
        setSupportActionBar(toolbar)

        fab.setOnClickListener {
            createUnit(labelEditText.text.toString())
        }
    }

    private fun createUnit(label: String) {
        val body = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("label", label)
                .addFormDataPart("step", "1")
                .build()

        RetrofitFactory.getClient(UnitService::class.java, this@NewUnitActivity)
                .createUnit(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object: Subscriber<Unit>() {
                    override fun onCompleted() { }

                    override fun onNext(t: Unit?) {
                        val intent = Intent()

                        intent.putExtra("unit", t)
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                    }

                    override fun onError(e: Throwable) {
                        Toast.makeText(this@NewUnitActivity, e.message, Toast.LENGTH_LONG).show()
                    }

                })
    }

    companion object {
        private const val TAG = "NewUnitActivity"
    }
}
