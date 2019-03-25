package com.refrii.client.newbox

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.refrii.client.App
import com.refrii.client.R
import kotlinx.android.synthetic.main.activity_new_box.*
import kotterknife.bindView
import javax.inject.Inject

class NewBoxActivity : AppCompatActivity(), NewBoxContract.View {

    private val mFab: FloatingActionButton by bindView(R.id.fab)
    private val mNameEditText: EditText by bindView(R.id.nameEditText)
    private val mNoticeEditText: EditText by bindView(R.id.noticeEditText)
    private val mPregressBar: ProgressBar by bindView(R.id.progressBar)

    @Inject
    lateinit var mPresenter: NewBoxContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (application as App).getComponent().inject(this)
        setContentView(R.layout.activity_new_box)
        setSupportActionBar(toolbar)

        title = "カテゴリの作成"

        mFab.setOnClickListener { createBox() }
        mPregressBar.visibility = View.GONE
    }

    override fun onStart() {
        super.onStart()

        mPresenter.takeView(this)
    }

    override fun onLoading() {
        mFab.hide()
        mPregressBar.visibility = View.VISIBLE
    }

    override fun onLoaded() {
        mFab.hide()
        mPregressBar.visibility = View.GONE
    }

    override fun showToast(message: String?) {
        message ?: return

        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun onCreateSuccess() {
        val intent = Intent()

        setResult(Activity.RESULT_OK, intent)
        finish()
    }


    private fun createBox() {
        val name = mNameEditText.text.toString()
        val notice = mNoticeEditText.text.toString()

        mPresenter.createBox(name, notice)
    }
}
