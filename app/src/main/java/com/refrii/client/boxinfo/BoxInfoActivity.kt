package com.refrii.client.boxinfo

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.refrii.client.App
import com.refrii.client.R
import com.refrii.client.data.api.models.Box
import com.refrii.client.data.api.models.User
import com.refrii.client.dialogs.EditTextDialogFragment
import com.refrii.client.dialogs.UserPickerDialogFragment
import kotterknife.bindView
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class BoxInfoActivity : AppCompatActivity(), BoxInfoContract.View {

    private val mName: TextView by bindView(R.id.nameTextView)
    private val mNotice: TextView by bindView(R.id.noticeTextView)
    private val mOwner: TextView by bindView(R.id.ownerTextView)
    private val mSharedUsers: TextView by bindView(R.id.sharedUsersTextView)
    private val mCreated: TextView by bindView(R.id.createdTextView)
    private val mUpdated: TextView by bindView(R.id.updatedTextView)
    private val mEditName: ImageView by bindView(R.id.editNameImageView)
    private val mEditNotice: ImageView by bindView(R.id.editNoticeImageView)
    private val mShare: ImageView by bindView(R.id.shareImageView)
    private val mFab: FloatingActionButton by bindView(R.id.floatingActionButton)
    private val mToolbar: Toolbar by bindView(R.id.toolbar)
    private val mEditedMessage: TextView by bindView(R.id.editedMessageTextView)
    private val mProgressBar: ProgressBar by bindView(R.id.progressBar)

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

        mEditName.setOnClickListener { mPresenter.editName() }
        mEditNotice.setOnClickListener { mPresenter.editNotice() }
        mShare.setOnClickListener { mPresenter.editSharedUsers() }
        mFab.setOnClickListener { mPresenter.updateBox() }
    }

    override fun onStart() {
        super.onStart()

        val boxId = intent.getIntExtra(getString(R.string.key_box_id), 0)

        onBeforeEdit()
        mPresenter.takeView(this)
        mPresenter.getBox(boxId)
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

        mName.text = box.name
        mNotice.text = box.notice
        box.owner?.let { mOwner.text = it.name }
        mCreated.text = formatter.format(box.createdAt)
        mUpdated.text = formatter.format(box.updatedAt)
        mSharedUsers.text = box.invitedUsers
                ?.map { it.name }
                ?.joinToString(System.getProperty("line.separator"))
    }

    override fun showEditNameDialog(name: String?) {
        name ?: return

        val fragment = EditTextDialogFragment.newInstance("Name", name)
        fragment.setTargetFragment(null, EDIT_NAME_REQUEST_CODE)
        fragment.show(fragmentManager, "edit_name")
    }

    override fun showEditNoticeDialog(notice: String?) {
        notice ?: return

        val fragment = EditTextDialogFragment.newInstance("Notice", notice, true)

        fragment.setTargetFragment(null, EDIT_NOTICE_REQUEST_CODE)
        fragment.show(fragmentManager, "edit_notice")
    }

    override fun showEditSharedUsersDialog(users: List<User>?) {
        users ?: return

        val fragment = UserPickerDialogFragment.newInstance("Shared users", users)

        fragment.setTargetFragment(null, EDIT_SHARED_USERS_REQUEST_CODE)
        fragment.show(fragmentManager, "contact_us")
    }

    override fun showSnackbar(message: String?) {
        message ?: return

        Snackbar.make(mName, message, Snackbar.LENGTH_LONG).show()
    }

    override fun showToast(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun onLoaded() {
        mProgressBar.visibility = View.GONE
    }

    override fun onLoading() {
        mProgressBar.visibility = View.VISIBLE
    }

    override fun onBeforeEdit() {
        mFab.visibility = View.GONE
        mEditedMessage.visibility = View.GONE
    }

    override fun onEdited() {
        mFab.visibility = View.VISIBLE
        mEditedMessage.visibility = View.VISIBLE
    }

    companion object {
        private const val EDIT_NAME_REQUEST_CODE = 101
        private const val EDIT_NOTICE_REQUEST_CODE = 102
        private const val EDIT_SHARED_USERS_REQUEST_CODE = 103
    }
}
