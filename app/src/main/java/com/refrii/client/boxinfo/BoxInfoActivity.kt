package com.refrii.client.boxinfo

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.refrii.client.App
import com.refrii.client.R
import com.refrii.client.data.api.models.Box
import com.refrii.client.data.api.models.User
import com.refrii.client.dialogs.UserPickerDialogFragment
import kotterknife.bindView
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class BoxInfoActivity : AppCompatActivity(), BoxInfoContract.View {

    private val mNameEditText: EditText by bindView(R.id.nameEditText)
    private val mNoticeEditText: EditText by bindView(R.id.noticeEditText)
    private val mOwnerText: TextView by bindView(R.id.ownerTextView)
    //    private val mSharedUsers: TextView by bindView(R.id.sharedUsersTextView)
    private val mCreatedText: TextView by bindView(R.id.createdTextView)
    private val mUpdatedText: TextView by bindView(R.id.updatedTextView)
    private val mFab: FloatingActionButton by bindView(R.id.floatingActionButton)
    private val mToolbar: Toolbar by bindView(R.id.toolbar)
    private val mProgressBar: ProgressBar by bindView(R.id.progressBar)

    private val mNameTextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {}
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            mPresenter.updateName(s.toString())
        }
    }
    private val mNoticeTextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {}
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            mPresenter.updateNotice(s.toString())
        }
    }

    @Inject
    lateinit var mPresenter: BoxInfoPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (application as App).getComponent().inject(this)

        setContentView(R.layout.activity_box_info)
        setSupportActionBar(mToolbar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeButtonEnabled(true)
        }

        mNameEditText.addTextChangedListener(mNameTextWatcher)
        mNoticeEditText.addTextChangedListener(mNoticeTextWatcher)
//        mShare.setOnClickListener { mPresenter.editSharedUsers() }
        mFab.setOnClickListener { mPresenter.updateBox() }
    }

    override fun onStart() {
        super.onStart()

        val boxId = intent.getIntExtra(getString(R.string.key_box_id), 0)

        mPresenter.takeView(this)
        mPresenter.getBox(boxId)
        onLoaded()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        data ?: return

        when(requestCode) {
            EDIT_NAME_REQUEST_CODE -> mPresenter.updateName(data.getStringExtra("text"))
            EDIT_NOTICE_REQUEST_CODE -> mPresenter.updateNotice(data.getStringExtra("text"))
        }
    }

    override fun setBox(box: Box?) {
        box ?: return

        mToolbar.title = box.name
        setSupportActionBar(mToolbar)

        val formatter = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())

        mNameEditText.setText(box.name)
        mNoticeEditText.setText(box.notice)
        box.owner?.let { mOwnerText.text = it.name }
        mCreatedText.text = formatter.format(box.createdAt)
        mUpdatedText.text = formatter.format(box.updatedAt)
//        mSharedUsers.text = box.invitedUsers
//                ?.map { it.name }
//                ?.joinToString(System.getProperty("line.separator"))
    }

    override fun showEditSharedUsersDialog(users: List<User>?) {
        users ?: return

        val fragment = UserPickerDialogFragment.newInstance("Shared users", users)

        fragment.setTargetFragment(null, EDIT_SHARED_USERS_REQUEST_CODE)
        fragment.show(fragmentManager, "contact_us")
    }

    override fun showSnackbar(message: String?) {
        message ?: return

        Snackbar.make(mNameEditText, message, Snackbar.LENGTH_LONG).show()
    }

    override fun showToast(message: String?) {
        message ?: return

        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun onLoaded() {
        mProgressBar.visibility = View.GONE
    }

    override fun onLoading() {
        mProgressBar.visibility = View.VISIBLE
    }

    override fun onDeleteCompleted() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object {
        private const val EDIT_NAME_REQUEST_CODE = 101
        private const val EDIT_NOTICE_REQUEST_CODE = 102
        private const val EDIT_SHARED_USERS_REQUEST_CODE = 103
    }
}
