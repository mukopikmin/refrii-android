package com.refrii.client.newunit

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.refrii.client.App
import com.refrii.client.R
import kotterknife.bindView
import javax.inject.Inject

class NewUnitActivity : AppCompatActivity(), NewUnitContract.View {

    private val toolbar: Toolbar by bindView(R.id.toolbar)
    private val labelEditText: EditText by bindView(R.id.labelEditText)
    private val stepEditText: EditText by bindView(R.id.stepEditText)
    private val fab: FloatingActionButton by bindView(R.id.fab)
    private val progressBar: ProgressBar by bindView(R.id.progressBar)

    @Inject
    lateinit var mPresenter: NewUnitContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (application as App).getComponent().inject(this)

        setContentView(R.layout.activity_new_unit)
        setSupportActionBar(toolbar)
        toolbar.title = "Add unit"
        setSupportActionBar(toolbar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeButtonEnabled(true)
        }

        fab.setOnClickListener {
            val label = labelEditText.text.toString()
            val step = stepEditText.text.toString().toDouble()

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

    override fun showProgressBar() {
        progressBar.visibility = View.VISIBLE
    }

    override fun hideProgressBar() {
        progressBar.visibility = View.GONE
    }

    override fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
