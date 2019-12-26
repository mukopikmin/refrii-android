package com.refrii.client.boxinfo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import butterknife.BindView
import butterknife.ButterKnife
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.refrii.client.App
import com.refrii.client.R
import com.refrii.client.data.models.Box
import com.refrii.client.data.models.Invitation
import com.refrii.client.data.models.User
import com.refrii.client.dialogs.ConfirmDialogFragment
import com.refrii.client.dialogs.InviteUserDialogFragment
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class BoxInfoActivity : AppCompatActivity(), BoxInfoContract.View {

    @BindView(R.id.nameEditText)
    lateinit var mNameEditText: EditText
    @BindView(R.id.noticeEditText)
    lateinit var mNoticeEditText: EditText
    @BindView(R.id.ownerTextView)
    lateinit var mOwnerText: TextView
    @BindView(R.id.sharedUsersLayout)
    lateinit var mSharedUsersRecycler: androidx.recyclerview.widget.RecyclerView
    @BindView(R.id.createdTextView)
    lateinit var mCreatedText: TextView
    @BindView(R.id.updatedTextView)
    lateinit var mUpdatedText: TextView
    @BindView(R.id.floatingActionButton)
    lateinit var mFab: FloatingActionButton
    @BindView(R.id.toolbar)
    lateinit var mToolbar: Toolbar
    @BindView(R.id.progressBar)
    lateinit var mProgressBar: ProgressBar
    @BindView(R.id.addSharedUserLayout)
    lateinit var mInviteLayout: ConstraintLayout

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
        ButterKnife.bind(this)
        setSupportActionBar(mToolbar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeButtonEnabled(true)
        }

        mSharedUsersRecycler.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
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
            REMOVE_INVITATION_REQUEST_CODE -> mPresenter.removeInvitation()
            EDIT_SHARED_USERS_REQUEST_CODE -> onInviteRequested(data)
        }
    }

    override fun removeInvitation(boxName: String?, invitation: Invitation) {
        boxName ?: return

        val user = invitation.user
        val userId = invitation.user?.id ?: return
        val message = "${user?.name} への $boxName の共有を解除していいですか？"
        val fragment = ConfirmDialogFragment.newInstance("共有の解除", message, userId)

        fragment.setTargetFragment(null, REMOVE_INVITATION_REQUEST_CODE)
        fragment.show(supportFragmentManager, "delete_invitation")
    }

    private fun onInviteRequested(data: Intent?) {
        data ?: return

        val email = data.getStringExtra("email")

        mPresenter.createInvitation(email)
    }

    override fun setBox(box: Box?) {
        box ?: return

        val formatter = SimpleDateFormat(getString(R.string.format_datetime), Locale.getDefault())

        mToolbar.title = box.name
        setSupportActionBar(mToolbar)

        mNameEditText.setText(box.name)
        mNoticeEditText.setText(box.notice)
        mCreatedText.text = formatter.format(box.createdAt)
        mUpdatedText.text = formatter.format(box.updatedAt)

        box.owner?.let { mOwnerText.text = it.name }
    }

    override fun setInvitations(invitations: List<Invitation>) {
        if (mSharedUsersRecycler.adapter == null) {
            val adapter = InvitationsRecyclerViewAdapter(invitations)

            adapter.setDeinviteClickListener(View.OnClickListener {
                val position = mSharedUsersRecycler.getChildAdapterPosition(it)
                val user = adapter.getItemAtPosition(position)

                mPresenter.confirmRemovingInvitation(user)
            })
            mSharedUsersRecycler.adapter = adapter
        } else {
            (mSharedUsersRecycler.adapter as InvitationsRecyclerViewAdapter).setInvitations(invitations)
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
        private const val REMOVE_INVITATION_REQUEST_CODE = 105
    }
}
