package com.refrii.client.welcome

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import butterknife.BindView
import butterknife.ButterKnife
import com.refrii.client.App
import com.refrii.client.R
import javax.inject.Inject

class WelcomeActivity : Activity(), WelcomeContract.View {

    @BindView(R.id.createButton)
    lateinit var mCreateButton: ImageButton
    @BindView(R.id.progressbar)
    lateinit var mProgressBar: View
    @BindView(R.id.nameEditText)
    lateinit var mNameEditText: EditText

    @Inject
    lateinit var mPresenter: WelcomePresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (application as App).getComponent().inject(this)
        setContentView(R.layout.activity_welcome)
        ButterKnife.bind(this)

        mProgressBar.visibility = View.GONE
        mCreateButton.setOnClickListener { createBox() }
    }

    override fun onStart() {
        super.onStart()

        mPresenter.takeView(this)
    }

    override fun onLoading() {
        mProgressBar.visibility = View.VISIBLE
    }

    override fun onLoaded() {
        mProgressBar.visibility = View.GONE
    }

    override fun onCreateBoxCompleted() {
        finish()
    }

    override fun showToast(message: String?) {
        message ?: return

        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun createBox() {
        val name = mNameEditText.text.toString()

        if (name.isEmpty()) {
            showToast("カテゴリ名を指定してください")
        } else {
            mPresenter.createBox(name)
        }
    }
}
