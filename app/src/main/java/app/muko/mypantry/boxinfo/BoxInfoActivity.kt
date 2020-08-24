package app.muko.mypantry.boxinfo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import app.muko.mypantry.App
import app.muko.mypantry.R
import app.muko.mypantry.data.models.Box
import app.muko.mypantry.data.models.Invitation
import app.muko.mypantry.dialogs.ConfirmDialogFragment
import app.muko.mypantry.invitations.InvitationListActivity
import butterknife.BindView
import butterknife.ButterKnife
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class BoxInfoActivity : AppCompatActivity(), BoxInfoContract.View {

    @BindView(R.id.nameEditText)
    lateinit var nameEditText: EditText

    @BindView(R.id.noticeEditText)
    lateinit var noticeEditText: EditText

    @BindView(R.id.ownerTextView)
    lateinit var ownerNameText: TextView

    @BindView(R.id.createdTextView)
    lateinit var createdAtText: TextView

    @BindView(R.id.updatedTextView)
    lateinit var updatedAtText: TextView

    @BindView(R.id.floatingActionButton)
    lateinit var fab: FloatingActionButton

    @BindView(R.id.toolbar)
    lateinit var toolbar: Toolbar

    @BindView(R.id.progressBar)
    lateinit var progressBar: ProgressBar

    @BindView(R.id.sharedCountTextView)
    lateinit var invitationsStatusText: TextView

    @Inject
    lateinit var presenter: BoxInfoPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (application as App).getComponent().inject(this)

        setContentView(R.layout.activity_box_info)
        ButterKnife.bind(this)
        setSupportActionBar(toolbar)

        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeButtonEnabled(true)
        }

        fab.setOnClickListener { updateBox() }
        invitationsStatusText.setOnClickListener { presenter.showInvitations() }
    }

    override fun onStart() {
        super.onStart()

        val boxId = intent.getIntExtra(getString(R.string.key_box_id), 0)

        presenter.init(this, boxId)
        presenter.getBox(boxId)
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
            R.id.menu_invite -> presenter.showInvitations()
            R.id.menu_remove_box -> presenter.confirmRemovingBox()
            else -> result = super.onOptionsItemSelected(item)
        }

        return result
    }

    override fun showInvitations(box: Box) {
        val intent = Intent(this, InvitationListActivity::class.java)

        intent.putExtra(getString(R.string.key_box_id), box.id)
        startActivity(intent)
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

        when (requestCode) {
            REMOVE_BOX_REQUEST_CODE -> presenter.removeBox()
        }
    }

    override fun setBox(box: Box?) {
        box ?: return

        val formatter = SimpleDateFormat(getString(R.string.format_datetime), Locale.getDefault())

        toolbar.title = box.name
        setSupportActionBar(toolbar)

        nameEditText.setText(box.name)
        noticeEditText.setText(box.notice)
        createdAtText.text = formatter.format(box.createdAt)
        updatedAtText.text = formatter.format(box.updatedAt)

        box.owner?.let { ownerNameText.text = it.name }
    }

    override fun setInvitations(invitations: List<Invitation>) {
        invitationsStatusText.text = getString(R.string.text_invitations_status, invitations.count())
    }

    override fun showSnackbar(message: String?) {
        message ?: return

        Snackbar.make(nameEditText, message, Snackbar.LENGTH_LONG).show()
    }

    override fun showToast(message: String?) {
        message ?: return

        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun onLoaded() {
        progressBar.visibility = View.GONE
    }

    override fun onLoading() {
        progressBar.visibility = View.VISIBLE
    }

    override fun onDeleteCompleted(name: String?) {
        val intent = Intent()

        intent.putExtra("key_box_name", name)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    private fun updateBox() {
        val name = nameEditText.text.toString()
        val notice = noticeEditText.text.toString()

        presenter.updateBox(name, notice)
    }

    companion object {
        private const val REMOVE_BOX_REQUEST_CODE = 104
    }
}
