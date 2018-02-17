package com.refrii.client.views.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.refrii.client.R
import com.refrii.client.RealmUtil
import com.refrii.client.models.Unit
import com.refrii.client.services.RetrofitFactory
import com.refrii.client.services.UnitService
import com.refrii.client.views.fragments.EditDoubleDialogFragment
import com.refrii.client.views.fragments.EditTextDialogFragment
import io.realm.Realm
import kotterknife.bindView
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*

class UnitActivity : AppCompatActivity() {

    private val toolbar: Toolbar by bindView(R.id.toolbar)
    private val labelTextView: TextView by bindView(R.id.labelTextView)
    private val stepTextView: TextView by bindView(R.id.stepTextView)
    private val createdTextView: TextView by bindView(R.id.createdTextView)
    private val updatedTextView: TextView by bindView(R.id.updatedTextView)
    private val labelImageView: ImageView by bindView(R.id.labelImageView)
    private val stepImageView: ImageView by bindView(R.id.stepImageView)
    private val fab: FloatingActionButton by bindView(R.id.fab)

    private lateinit var mRealm: Realm
    private var mUnit: Unit? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_unit)
        setSupportActionBar(toolbar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeButtonEnabled(true)
        }

        Realm.init(this)
        mRealm = RealmUtil.getInstance()

        val intent = intent
        val unitId = intent.getIntExtra("unit_id", 0)

        mUnit = getUnit(unitId)
        mUnit?.let { setUnit(it) }

        onLoaded()

        labelImageView.setOnClickListener {
            mUnit?.let {
                it.label?.let {
                    val fragment = EditTextDialogFragment.newInstance("Label", it)

                    fragment.setTargetFragment(null, EDIT_LABEL_REQUEST_CODE)
                    fragment.show(fragmentManager, "edit_label")
                }
            }
        }

        stepImageView.setOnClickListener {
            mUnit?.let {
                val fragment = EditDoubleDialogFragment.newInstance("Step size", it.step)

                fragment.setTargetFragment(null, EDIT_STEP_REQUEST_CODE)
                fragment.show(fragmentManager, "edit_step")
            }
        }

        fab.setOnClickListener {
            mUnit?.let { updateUnit(it) }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) return
        data ?: return

        when (requestCode) {
            EDIT_LABEL_REQUEST_CODE -> {
                mRealm.executeTransaction { mUnit?.label = data.getStringExtra("text") }
                mUnit?.let { setUnit(it) }
                onEdited()
            }
            EDIT_STEP_REQUEST_CODE -> {
                mRealm.executeTransaction { mUnit?.step = data.getDoubleExtra("number", 0.toDouble()) }
                mUnit?.let { setUnit(it) }
                onEdited()
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

    private fun getUnit(id: Int): Unit? =
            mRealm.where(Unit::class.java)?.equalTo("id", id)?.findFirst()

    private fun setUnit(unit: Unit) {
        val formatter = SimpleDateFormat("yyyy/MM/dd HH/mm", Locale.getDefault())

        labelTextView.text = unit.label
        stepTextView.text = unit.step.toString()
        createdTextView.text = formatter.format(unit.createdAt)
        updatedTextView.text = formatter.format(unit.updatedAt)
    }

    private fun updateUnit(unit: Unit) {
        Log.e(TAG, "" + unit.id)
        RetrofitFactory.getClient(UnitService::class.java, this@UnitActivity)
                .updateUnit(unit.id, unit.toMultipartBody())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object: Subscriber<Unit>() {
                    override fun onCompleted() {
                        Snackbar.make(fab, "Replace with your own action", Snackbar.LENGTH_LONG)
                                .setAction("Action", null)
                                .show()
                    }

                    override fun onNext(t: Unit) {
                    }

                    override fun onError(e: Throwable) {
                        Toast.makeText(this@UnitActivity, e.message, Toast.LENGTH_LONG).show()
                    }

                })
    }

    private fun onLoaded() {
        fab.hide()
    }

    private fun onEdited() {
        fab.show()
    }

    companion object {
        private const val TAG = "UnitActivity"
        private const val EDIT_LABEL_REQUEST_CODE = 101
        private const val EDIT_STEP_REQUEST_CODE = 121
    }
}
