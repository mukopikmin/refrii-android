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
    lateinit var mNameEditText: EditText

    @BindView(R.id.noticeEditText)
    lateinit var mNoticeEditText: EditText

    @BindView(R.id.ownerTextView)
    lateinit var mOwnerText: TextView

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

    @BindView(R.id.sharedCountTextView)
    lateinit var mSharedCountText: TextView

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

        mFab.setOnClickListener { updateBox() }
        mSharedCountText.setOnClickListener { mPresenter.showInvitations() }
    }

    private fun updateBox() {
        val name = mNameEditText.text.toString()
        val notice = mNoticeEditText.text.toString()

        mPresenter.updateBox(name, notice)
    }

    override fun onStart() {
        super.onStart()

        val boxId = intent.getIntExtra(getString(R.string.key_box_id), 0)

        mPresenter.init(this, boxId)
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
            R.id.menu_invite -> mPresenter.showInvitations()
            R.id.menu_remove_box -> mPresenter.confirmRemovingBox()
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
            REMOVE_BOX_REQUEST_CODE -> mPresenter.removeBox()
        }
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
        mSharedCountText.text = "${invitations.count()} 人のユーザーと共有しています"
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
        private const val REMOVE_BOX_REQUEST_CODE = 104
    }
}
