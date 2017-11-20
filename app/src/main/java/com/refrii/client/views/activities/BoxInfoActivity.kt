package com.refrii.client.views.activities

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.refrii.client.BasicCallback
import com.refrii.client.R
import com.refrii.client.RetrofitFactory
import com.refrii.client.models.Box
import com.refrii.client.services.BoxService
import com.refrii.client.views.fragments.EditTextDialogFragment
import com.refrii.client.views.fragments.UserPickerDialogFragment
import io.realm.Realm
import io.realm.RealmConfiguration
import kotterknife.bindView
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Response
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

    private var mRealm: Realm? = null
    private var mBox: Box? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_box_info)

        Realm.setDefaultConfiguration(RealmConfiguration.Builder(this).build())
        mRealm = Realm.getDefaultInstance()

        val boxId = intent.getIntExtra("box_id", 0)
        mBox = mRealm!!.where(Box::class.java)
                .equalTo("id", boxId)
                .findFirst()

        if (mBox == null) {
            finish()
            return
        }

        mBox?.let {
            setBox(it)
            syncBox(it)
        }

        editNameImageView.setOnClickListener {
            val fragment = EditTextDialogFragment.newInstance("Name", mBox!!.name!!)
            fragment.setTargetFragment(null, EDIT_NAME_REQUEST_CODE)
            fragment.show(fragmentManager, "edit_name")
        }

        editNoticeImageView.setOnClickListener {
            val fragment = EditTextDialogFragment.newInstance("Notice", mBox!!.notice!!, true)
            fragment.setTargetFragment(null, EDIT_NOTICE_REQUEST_CODE)
            fragment.show(fragmentManager, "edit_notice")
        }

        shareImageView.setOnClickListener {
            val fragment = UserPickerDialogFragment.newInstance("Shared users", mBox!!.invitedUsers!!)
            fragment.setTargetFragment(null, EDIT_SHARED_USERS_REQUEST_CODE)
            fragment.show(fragmentManager, "contact_us")
        }

        fab.setOnClickListener { updateBox(mBox!!) }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when(requestCode) {
            EDIT_NAME_REQUEST_CODE -> {
                data ?: return

                mRealm?.let { realm ->
                    realm.executeTransaction {
                        mBox?.name = data.getStringExtra("text")
                    }
                    setBox(mBox!!)
                }
            }
            EDIT_NOTICE_REQUEST_CODE -> {
                data ?: return

                mRealm?.let { realm ->
                    realm.executeTransaction {
                        mBox?.notice = data.getStringExtra("text")
                    }
                    setBox(mBox!!)
                }
            }
        }
    }

    private fun setBox(box: Box) {
        toolbar.title = box.name
        setSupportActionBar(toolbar)

        val formatter = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())

        nameTextView.text = box.name
        noticeTextView.text = box.notice
        createdUserTextView.text = box.owner!!.name
        createdAtTextView.text = formatter.format(box.createdAt)
        updatedAtTextView.text = formatter.format(box.updatedAt)

        var sharedUsers = ""
        for (user in box.invitedUsers!!) {
            sharedUsers += user.name
            if (box.invitedUsers!![box.invitedUsers!!.size - 1] !== user) {
                sharedUsers += System.getProperty("line.separator")
            }
        }
        sharedUsersTextView.text = sharedUsers
    }

    private fun updateBox(box: Box) {
        val service = RetrofitFactory.getClient(BoxService::class.java, this@BoxInfoActivity)
        val body = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("name", box.name!!)
                .addFormDataPart("notice", box.notice!!)
                .build()
        val call = service.updateBox(box.id, body)
        call.enqueue(object : BasicCallback<Box>(this@BoxInfoActivity) {
            override fun onResponse(call: Call<Box>, response: Response<Box>) {
                super.onResponse(call, response)

                if (response.code() == 200) {
                    val view = findViewById<FloatingActionButton>(R.id.floatingActionButton)
                    Snackbar.make(view, "This box is successfully updated", Snackbar.LENGTH_LONG)
                            .setAction("Dismiss", null).show()
                }
            }
        })
    }

    private fun syncBox(box: Box) {
        RetrofitFactory.getClient(BoxService::class.java, this)
                .getBox(box.id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object: Subscriber<Box>() {
                    override fun onError(e: Throwable?) {
                        e ?: return

                        Toast.makeText(this@BoxInfoActivity, e.toString(), Toast.LENGTH_SHORT).show()
                    }

                    override fun onCompleted() { }

                    override fun onNext(t: Box?) {
                        t ?: return

                        Log.d(TAG, "Box synced")

                        mRealm?.let { realm ->
                            realm.executeTransaction {
                                realm.copyToRealmOrUpdate(t)
                            }

                            setBox(t)
                        }
                    }

                })
    }

    companion object {
        private val TAG = "FoodInfoActivity"
        private val EDIT_NAME_REQUEST_CODE = 101
        private val EDIT_NOTICE_REQUEST_CODE = 102
        private val EDIT_SHARED_USERS_REQUEST_CODE = 103
    }
}
