package com.refrii.client.newunit

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import butterknife.BindView
import butterknife.ButterKnife
import com.refrii.client.App
import com.refrii.client.R
import com.refrii.client.data.api.models.Unit
import javax.inject.Inject

class NewUnitActivity : AppCompatActivity(), NewUnitContract.View {

    @BindView(R.id.toolbar)
    lateinit var mToolbar: Toolbar
    @BindView(R.id.labelEditText)
    lateinit var mLabelEditText: EditText
    @BindView(R.id.stepEditText)
    lateinit var mStepEditText: EditText
    @BindView(R.id.fab)
    lateinit var mFab: FloatingActionButton
    @BindView(R.id.progressBar)
    lateinit var mProgressBar: ProgressBar

    @Inject
    lateinit var mPresenter: NewUnitContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (application as App).getComponent().inject(this)

        setContentView(R.layout.activity_new_unit)
        ButterKnife.bind(this)
        hideProgressBar()
        setSupportActionBar(mToolbar)
        mToolbar.title = "Add unit"
        setSupportActionBar(mToolbar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeButtonEnabled(true)
        }

        mFab.setOnClickListener {
            val label = mLabelEditText.text.toString()
            val step = mStepEditText.text.toString().toDouble()

            mPresenter.createUnit(label, step)
        }
    }

    override fun onStart() {
        super.onStart()

        mPresenter.takeView(this)
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

    override fun onCreateCompleted(unit: Unit?) {
        unit ?: return

        val intent = Intent()

        intent.putExtra(getString(R.string.key_unit_id), unit.id)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    override fun showProgressBar() {
        mProgressBar.visibility = View.VISIBLE
    }

    override fun hideProgressBar() {
        mProgressBar.visibility = View.GONE
    }

    override fun showToast(message: String?) {
        message ?: return
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
