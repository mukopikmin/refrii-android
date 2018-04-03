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

    private val nameTextView: TextView by bindView(R.id.shareTextView)
    private val noticeTextView: TextView by bindView(R.id.noticeTextView)
    private val createdUserTextView: TextView by bindView(R.id.ownerBoxInfoTextView)
    private val sharedUsersTextView: TextView by bindView(R.id.sharedUsersTextView)
    private val createdAtTextView: TextView by bindView(R.id.createdAtBoxInfoTextView)
    private val updatedAtTextView: TextView by bindView(R.id.updatedAtBoxInfoTextView)
    private val editNameImageView: ImageView by bindView(R.id.editNameImageView)
    private val editNoticeImageView: ImageView by bindView(R.id.editNoticeImageView)
    private val shareImageView: ImageView by bindView(R.id.shareImageView)
    private val fab: FloatingActionButton by bindView(R.id.floatingActionButton)
    private val toolbar: Toolbar by bindView(R.id.toolbar)

    @Inject
    lateinit var mPresenter: BoxInfoPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (application as App).getComponent().inject(this)

        setContentView(R.layout.activity_box_info)
        setSupportActionBar(toolbar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeButtonEnabled(true)
        }

        editNameImageView.setOnClickListener { mPresenter.editName() }
        editNoticeImageView.setOnClickListener { mPresenter.editNotice() }
        shareImageView.setOnClickListener { mPresenter.editSharedUsers() }
        fab.setOnClickListener { mPresenter.updateBox() }
    }

    override fun onStart() {
        super.onStart()

        val boxId = intent.getIntExtra("box_id", 0)

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
            EDIT_NAME_REQUEST_CODE -> {
                mPresenter.updateName(data.getStringExtra("text"))
                onEdited()
            }
            EDIT_NOTICE_REQUEST_CODE -> {
                mPresenter.updateNotice(data.getStringExtra("text"))
                onEdited()
            }
        }
    }

    override fun setBox(box: Box?) {
        box ?: return

        toolbar.title = box.name
        setSupportActionBar(toolbar)

        val formatter = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())

        nameTextView.text = box.name
        noticeTextView.text = box.notice
        box.owner?.let { createdUserTextView.text = it.name }
        createdAtTextView.text = formatter.format(box.createdAt)
        updatedAtTextView.text = formatter.format(box.updatedAt)
        sharedUsersTextView.text = box.invitedUsers
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

        Snackbar.make(nameTextView, message, Snackbar.LENGTH_LONG).show()
    }

    override fun showToast(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun onLoaded() {
        fab.visibility = View.GONE
    }

    override fun onLoading() {

    }

    private fun onEdited() {
        fab.visibility = View.VISIBLE
    }

    companion object {
        private const val EDIT_NAME_REQUEST_CODE = 101
        private const val EDIT_NOTICE_REQUEST_CODE = 102
        private const val EDIT_SHARED_USERS_REQUEST_CODE = 103
    }
}
