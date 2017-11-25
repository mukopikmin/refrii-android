package com.refrii.client.views.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.widget.EditText
import android.widget.Toast
import com.refrii.client.R
import com.refrii.client.factories.RetrofitFactory
import com.refrii.client.models.Unit
import com.refrii.client.services.UnitService
import io.realm.Realm
import io.realm.RealmConfiguration
import kotterknife.bindView
import okhttp3.MultipartBody
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class NewUnitActivity : AppCompatActivity() {

    private val toolbar: Toolbar by bindView(R.id.toolbar)
    private val labelEditText: EditText by bindView(R.id.labelEditText)
    private val stepEditText: EditText by bindView(R.id.stepEditText)
    private val fab: FloatingActionButton by bindView(R.id.fab)

    private lateinit var mRealm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_new_unit)
        setSupportActionBar(toolbar)

        Realm.setDefaultConfiguration(RealmConfiguration.Builder(this).build())
        mRealm = Realm.getDefaultInstance()

        fab.setOnClickListener {
            val label = labelEditText.text.toString()
            val step = stepEditText.text.toString().toDouble()

            createUnit(label, step)
        }
    }

    private fun createUnit(label: String, step: Double) {
        val body = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("label", label)
                .addFormDataPart("step", step.toString())
                .build()

        RetrofitFactory.getClient(UnitService::class.java, this@NewUnitActivity)
                .createUnit(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object: Subscriber<Unit>() {
                    override fun onCompleted() {
                        finish()
                    }

                    override fun onNext(t: Unit) {
                        val intent = Intent()

                        mRealm.executeTransaction { mRealm.copyToRealmOrUpdate(t) }

                        intent.putExtra("unit", t)
                        setResult(Activity.RESULT_OK, intent)
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
