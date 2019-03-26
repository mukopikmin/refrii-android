package com.refrii.client.boxinfo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
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
import com.refrii.client.dialogs.ConfirmDialogFragment
import com.refrii.client.dialogs.InviteUserDialogFragment
import kotterknife.bindView
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class BoxInfoActivity : AppCompatActivity(), BoxInfoContract.View {

    private val mNameEditText: EditText by bindView(R.id.nameEditText)
    private val mNoticeEditText: EditText by bindView(R.id.noticeEditText)
    private val mOwnerText: TextView by bindView(R.id.ownerTextView)
    private val mSharedUsersRecycler: RecyclerView by bindView(R.id.sharedUsersLayout)
    private val mCreatedText: TextView by bindView(R.id.createdTextView)
    private val mUpdatedText: TextView by bindView(R.id.updatedTextView)
    private val mFab: FloatingActionButton by bindView(R.id.floatingActionButton)
    private val mToolbar: Toolbar by bindView(R.id.toolbar)
    private val mProgressBar: ProgressBar by bindView(R.id.progressBar)
    private val mInviteLayout: ConstraintLayout by bindView(R.id.addSharedUserLayout)

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

        mSharedUsersRecycler.layoutManager = LinearLayoutManager(this)
        mNameEditText.addTextChangedListener(mNameTextWatcher)
        mNoticeEditText.addTextChangedListener(mNoticeTextWatcher)
        mFab.setOnClickListener { mPresenter.updateBox() }
        mInviteLayout.setOnClickListener { mPresenter.showInviteUserDialog() }
    }

    override fun onStart() {
        super.onStart()

        val boxId = intent.getIntExtra(getString(R.string.key_box_id), 0)

        mPresenter.takeView(this)
        mPresenter.getBox(boxId)
        onLoaded()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.box_info_menu, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        var result = true

        when (id) {
            android.R.id.home -> finish()
            R.id.menu_remove_box -> mPresenter.confirmRemovingBox()
            else -> result = super.onOptionsItemSelected(item)
        }

        return result
    }

    override fun removeBox(id: Int?, name: String?) {
        id ?: return
        name ?: return

        val fragment = ConfirmDialogFragment.newInstance(name, "削除していいですか？", id)

        fragment.setTargetFragment(null, REMOVE_BOX_REQUEST_CODE)
        fragment.show(supportFragmentManager, "delete_box")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        data ?: return

        when(requestCode) {
            REMOVE_BOX_REQUEST_CODE -> mPresenter.removeBox()
            EDIT_SHARED_USERS_REQUEST_CODE -> onInviteRequested(data)
        }
    }

    private fun onInviteRequested(data: Intent?) {
        data ?: return

        val email = data.getStringExtra("email")

        mPresenter.invite(email)
    }

    override fun setBox(box: Box?) {
        box ?: return

        val formatter = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())

        mToolbar.title = box.name
        setSupportActionBar(mToolbar)

        mNameEditText.setText(box.name)
        mNoticeEditText.setText(box.notice)
        mCreatedText.text = formatter.format(box.createdAt)
        mUpdatedText.text = formatter.format(box.updatedAt)

        box.owner?.let { mOwnerText.text = it.name }
        setSharedUsers(box.invitedUsers)
    }

    override fun setSharedUsers(users: List<User>?) {
        users ?: return

        if (mSharedUsersRecycler.adapter == null) {
            mSharedUsersRecycler.adapter = SharedUserRecyclerViewAdapter(users)
        } else {
            (mSharedUsersRecycler.adapter as SharedUserRecyclerViewAdapter).setUsers(users)
        }
    }

    override fun showInviteUserDialog(users: List<User>?) {
        users ?: return

        val fragment = InviteUserDialogFragment.newInstance(users)

        fragment.setTargetFragment(null, EDIT_SHARED_USERS_REQUEST_CODE)
        fragment.show(supportFragmentManager, "invite")
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

    override fun onDeleteCompleted(name: String?) {
        val intent = Intent()

        intent.putExtra("key_box_name", name)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    companion object {
        private const val EDIT_SHARED_USERS_REQUEST_CODE = 103
        private const val REMOVE_BOX_REQUEST_CODE = 104
    }
}
