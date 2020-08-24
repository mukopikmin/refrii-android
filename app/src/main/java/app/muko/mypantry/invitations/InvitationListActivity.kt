package app.muko.mypantry.invitations

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
import androidx.recyclerview.widget.RecyclerView
import app.muko.mypantry.App
import app.muko.mypantry.R
import app.muko.mypantry.data.models.Box
import app.muko.mypantry.data.models.Invitation
import app.muko.mypantry.dialogs.ConfirmDialogFragment
import app.muko.mypantry.dialogs.OptionsPickerDialogFragment
import butterknife.BindView
import butterknife.ButterKnife
import kotlinx.android.synthetic.main.activity_invitation_list.*
import javax.inject.Inject

class InvitationListActivity : AppCompatActivity(), InvitationListContract.View {

    @BindView(R.id.sharedUsersLayout)
    lateinit var invitedUserList: RecyclerView

    @BindView(R.id.progressBar)
    lateinit var progressBar: ProgressBar

    @BindView(R.id.emailEditText)
    lateinit var emailEditText: EditText

    @BindView(R.id.button)
    lateinit var submitButton: Button

    @Inject
    lateinit var presenter: InvitationListPresenter

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

        invitedUserList.addItemDecoration(androidx.recyclerview.widget.DividerItemDecoration(this, androidx.recyclerview.widget.DividerItemDecoration.VERTICAL))
        invitedUserList.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        submitButton.setOnClickListener { createInvitation() }
    }

    override fun onStart() {
        super.onStart()

        val boxId = intent.getIntExtra(getString(R.string.key_box_id), 0)

        presenter.init(this, boxId)
        presenter.getBox(boxId)
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
                    0 -> {
                        val id = data.getIntExtra("target_id", -1)

                        presenter.confirmRemovingInvitation(id)
                    }
                    else -> return
                }
            }
            REMOVE_INVITATION_REQUEST_CODE -> {
                val id = data.getIntExtra("target_id", -1)

                presenter.removeInvitation(id)
            }
        }
    }

    override fun setBox(box: Box) {
        title = box.name
    }

    override fun onLoading() {
        progressBar.visibility = View.VISIBLE
    }

    override fun onLoaded() {
        progressBar.visibility = View.GONE
    }

    override fun showSnackbar(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun setInvitations(invitations: List<Invitation>) {
        if (invitedUserList.adapter == null) {
            val adapter = InvitationsRecyclerViewAdapter(invitations)

            adapter.setOnLongClickListener(View.OnLongClickListener {
                val position = invitedUserList.getChildAdapterPosition(it)
                val invitation = adapter.getItemAtPosition(position)

                showOptionsDialog(invitation)

                true
            })
            invitedUserList.adapter = adapter
        } else {
            (invitedUserList.adapter as InvitationsRecyclerViewAdapter).setInvitations(invitations)
        }
    }

    override fun removeInvitation(invitation: Invitation, box: Box) {
        val user = invitation.user
        val userId = invitation.user.id
        val message = "${user.name} への ${box.name} の共有を解除していいですか？"
        val fragment = ConfirmDialogFragment.newInstance("共有の解除", message, invitation.id)

        fragment.setTargetFragment(null, REMOVE_INVITATION_REQUEST_CODE)
        fragment.show(supportFragmentManager, "delete_invitation")
    }

    override fun onInvitationCreated(box: Box) {
        emailEditText.editableText.clear()
        showSnackbar("${box.name} を共有しました")
    }

    private fun showOptionsDialog(invitation: Invitation) {
        val options = arrayOf(
                getString(R.string.text_remove),
                getString(R.string.message_cancel)
        )
        val fragment = OptionsPickerDialogFragment.newInstance(null, options, invitation.id)

        fragment.setTargetFragment(null, INVITATION_OPTIONS_REQUEST_CODE)
        fragment.show(supportFragmentManager, "invitation_option")
    }

    private fun createInvitation() {
        val email = emailEditText.text.toString()

        presenter.createInvitation(email)
    }

    companion object {
        private const val INVITATION_OPTIONS_REQUEST_CODE = 101
        private const val REMOVE_INVITATION_REQUEST_CODE = 102
    }
}
