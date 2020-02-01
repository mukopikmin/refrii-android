package com.refrii.client.invitations

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import butterknife.BindView
import butterknife.ButterKnife
import com.refrii.client.App
import com.refrii.client.R
import com.refrii.client.data.models.Box
import com.refrii.client.data.models.Invitation
import com.refrii.client.dialogs.ConfirmDialogFragment
import com.refrii.client.dialogs.OptionsPickerDialogFragment
import kotlinx.android.synthetic.main.activity_invitation_list.*
import javax.inject.Inject

class InvitationListActivity : AppCompatActivity(), InvitationListContract.View {

    @BindView(R.id.sharedUsersLayout)
    lateinit var mSharedUsersRecycler: androidx.recyclerview.widget.RecyclerView
    @BindView(R.id.progressBar)
    lateinit var mProgressBar: ProgressBar
    @BindView(R.id.emailEditText)
    lateinit var mEmailEditText: EditText
    @BindView(R.id.button)
    lateinit var mButton: Button

    @Inject
    lateinit var mPresenter: InvitationListPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (application as App).getComponent().inject(this)

        setContentView(R.layout.activity_invitation_list)
        ButterKnife.bind(this)
        setSupportActionBar(toolbar)

        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeButtonEnabled(true)
        }

        mSharedUsersRecycler.addItemDecoration(androidx.recyclerview.widget.DividerItemDecoration(this, androidx.recyclerview.widget.DividerItemDecoration.VERTICAL))
        mSharedUsersRecycler.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        mButton.setOnClickListener { createInvitation() }
    }

    private fun createInvitation() {
        val email = mEmailEditText.text.toString()

        mPresenter.createInvitation(email)
    }

    override fun onStart() {
        super.onStart()

        val boxId = intent.getIntExtra(getString(R.string.key_box_id), 0)

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

        if (resultCode != Activity.RESULT_OK || data == null) return

        when (requestCode) {
            INVITATION_OPTIONS_REQUEST_CODE -> {
                when (data.getIntExtra("option", -1)) {
                    0 -> mPresenter.confirmRemovingInvitation()
                    else -> return
                }
            }
            REMOVE_INVITATION_REQUEST_CODE -> mPresenter.removeInvitation()
        }
    }

    override fun setBox(box: Box) {
        title = box.name
    }

    override fun onLoading() {
        mProgressBar.visibility = View.VISIBLE
    }

    override fun onLoaded() {
        mProgressBar.visibility = View.GONE
    }

    override fun showSnackbar(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun setInvitations(invitations: List<Invitation>, box: Box) {
        if (mSharedUsersRecycler.adapter == null) {
            val adapter = InvitationsRecyclerViewAdapter(invitations, box)

            adapter.setOnLongClickListener(View.OnLongClickListener {
                val position = mSharedUsersRecycler.getChildAdapterPosition(it)
                val invitation = adapter.getItemAtPosition(position)

                mPresenter.showOptionsDialog(invitation)

                true
            })
            mSharedUsersRecycler.adapter = adapter
        } else {
            (mSharedUsersRecycler.adapter as InvitationsRecyclerViewAdapter).setInvitations(invitations)
        }
    }

    override fun removeInvitation(boxName: String, invitation: Invitation) {
        val user = invitation.user
        val userId = invitation.user?.id ?: return
        val message = "${user?.name} への $boxName の共有を解除していいですか？"
        val fragment = ConfirmDialogFragment.newInstance("共有の解除", message, userId)

        fragment.setTargetFragment(null, REMOVE_INVITATION_REQUEST_CODE)
        fragment.show(supportFragmentManager, "delete_invitation")
    }

    override fun onInvitationCreated(invitation: Invitation) {
        mEmailEditText.editableText.clear()
        showSnackbar("${invitation.user?.name} さんと共有しました")
    }

    override fun showOptionsDialog() {
        val options = arrayOf(
                "削除する",
                getString(R.string.message_cancel)
        )
        val fragment = OptionsPickerDialogFragment.newInstance(null, options, null)

        fragment.setTargetFragment(null, INVITATION_OPTIONS_REQUEST_CODE)
        fragment.show(supportFragmentManager, "invitation_option")
    }

    companion object {
        private const val INVITATION_OPTIONS_REQUEST_CODE = 101
        private const val REMOVE_INVITATION_REQUEST_CODE = 102
    }
}
