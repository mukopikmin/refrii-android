package com.refrii.client.views.activities

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.refrii.client.R
import com.refrii.client.RealmUtil
import com.refrii.client.models.Box
import com.refrii.client.services.BoxService
import com.refrii.client.services.RetrofitFactory
import com.refrii.client.views.fragments.EditTextDialogFragment
import com.refrii.client.views.fragments.UserPickerDialogFragment
import io.realm.Realm
import kotterknife.bindView
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*

class BoxInfoActivity : AppCompatActivity() {

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

    private lateinit var mRealm: Realm
    private var mBox: Box? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_box_info)
        setSupportActionBar(toolbar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeButtonEnabled(true)
        }

        Realm.init(this)
        mRealm = RealmUtil.getInstance()

        val boxId = intent.getIntExtra("box_id", 0)

        mBox = mRealm.where(Box::class.java)?.equalTo("id", boxId)?.findFirst()

        mBox?.let {
            setBox(it)
            syncBox(it)
        }

        onLoaded()

        editNameImageView.setOnClickListener {
            mBox?.let { box ->
                box.name?.let {
                    val fragment = EditTextDialogFragment.newInstance("Name", it)
                    fragment.setTargetFragment(null, EDIT_NAME_REQUEST_CODE)
                    fragment.show(fragmentManager, "edit_name")
                }
            }
        }

        editNoticeImageView.setOnClickListener {
            mBox?.let { box ->
                box.notice?.let {
                    val fragment = EditTextDialogFragment.newInstance("Notice", it, true)
                    fragment.setTargetFragment(null, EDIT_NOTICE_REQUEST_CODE)
                    fragment.show(fragmentManager, "edit_notice")
                }
            }
        }

        shareImageView.setOnClickListener {
            mBox?.let { box ->
                box.invitedUsers?.let {
                    val fragment = UserPickerDialogFragment.newInstance("Shared users", it)
                    fragment.setTargetFragment(null, EDIT_SHARED_USERS_REQUEST_CODE)
                    fragment.show(fragmentManager, "contact_us")
                }
            }
        }

        fab.setOnClickListener {
            mBox?.let { updateBox(it) }
        }
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
                mRealm.executeTransaction { mBox?.name = data.getStringExtra("text") }
                mBox?.let { setBox(it) }
                onEdited()
            }
            EDIT_NOTICE_REQUEST_CODE -> {
                mRealm.executeTransaction { mBox?.notice = data.getStringExtra("text") }
                mBox?.let { setBox(it) }
                onEdited()
            }
        }
    }

    private fun setBox(box: Box) {
        toolbar.title = box.name
        setSupportActionBar(toolbar)

        val formatter = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())

        nameTextView.text = box.name
        noticeTextView.text = box.notice
        box.owner?.let { createdUserTextView.text = it.name }
        createdAtTextView.text = formatter.format(box.createdAt)
        updatedAtTextView.text = formatter.format(box.updatedAt)
        sharedUsersTextView.text = box.invitedUsers?.map { it.name }?.joinToString(System.getProperty("line.separator"))
    }

    private fun updateBox(box: Box) {
        RetrofitFactory.getClient(BoxService::class.java, this@BoxInfoActivity)
                .updateBox(box.id, box.toMultipartBody())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object: Subscriber<Box>() {
                    override fun onNext(t: Box) {
                        setBox(t)
                    }

                    override fun onError(e: Throwable) {
                        Toast.makeText(this@BoxInfoActivity, e.message, Toast.LENGTH_LONG).show()
                    }

                    override fun onCompleted() {
                        onLoaded()

                        Snackbar.make(fab, "This box is successfully updated", Snackbar.LENGTH_LONG)
                                .setAction("Dismiss", null).show()
                    }
                })
    }

    private fun syncBox(box: Box) {
        RetrofitFactory.getClient(BoxService::class.java, this)
                .getBox(box.id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object: Subscriber<Box>() {
                    override fun onError(e: Throwable) {
                        Toast.makeText(this@BoxInfoActivity, e.toString(), Toast.LENGTH_SHORT).show()
                    }

                    override fun onCompleted() { }

                    override fun onNext(t: Box) {
                        Log.d(TAG, "Box synced")

                        mRealm.executeTransaction { mRealm.copyToRealmOrUpdate(t) }
                        setBox(t)
                    }

                })
    }

    private fun onLoaded() {
        fab.visibility = View.GONE
    }

    private fun onLoading() {

    }

    private fun onEdited() {
        fab.visibility = View.VISIBLE
    }

    companion object {
        private val TAG = "FoodInfoActivity"
        private val EDIT_NAME_REQUEST_CODE = 101
        private val EDIT_NOTICE_REQUEST_CODE = 102
        private val EDIT_SHARED_USERS_REQUEST_CODE = 103
    }
}
