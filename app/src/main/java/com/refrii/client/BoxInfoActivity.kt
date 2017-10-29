package com.refrii.client

import android.app.Dialog
import android.app.DialogFragment
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView

import java.text.SimpleDateFormat

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response

class BoxInfoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val box = intent.getSerializableExtra("box") as Box

        setContentView(R.layout.activity_box_info)
        val toolbar = findViewById<Toolbar>(R.id.toolbar) as Toolbar
        toolbar.title = box.name
        setSupportActionBar(toolbar)

        val nameTextView = findViewById<TextView>(R.id.shareTextView) as TextView
        val noticeTextView = findViewById<TextView>(R.id.noticeTextView) as TextView
        val createdUserTextView = findViewById<TextView>(R.id.ownerBoxInfoTextView) as TextView
        val sharedUsersTextView = findViewById<TextView>(R.id.sharedUsersTextView) as TextView
        val createdAtTextView = findViewById<TextView>(R.id.createdAtBoxInfoTextView) as TextView
        val updatedAtTextView = findViewById<TextView>(R.id.updatedAtBoxInfoTextView) as TextView
        val editNameImageView = findViewById<ImageView>(R.id.editNameImageView) as ImageView
        val editNoticeImageView = findViewById<ImageView>(R.id.editNoticeImageView) as ImageView
        val shareImageView = findViewById<ImageView>(R.id.shareImageView) as ImageView
        val fab = findViewById<FloatingActionButton>(R.id.floatingActionButton) as FloatingActionButton

        val formatter = SimpleDateFormat("yyyy/MM/dd HH:mm")

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

        editNameImageView.setOnClickListener {
            val fragment = EditNameDialogFragment()
            fragment.setTextView(nameTextView)
            fragment.setBox(box)
            fragment.show(fragmentManager, "edit_name")
        }

        editNoticeImageView.setOnClickListener {
            val fragment = EditNoticeDialogFragment()
            fragment.setTextView(noticeTextView)
            fragment.setBox(box)
            fragment.show(fragmentManager, "edit_notice")
        }

        shareImageView.setOnClickListener {
            val newFragment = SharedUsersDialogFragment()
            newFragment.setUsers(box.invitedUsers)
            newFragment.show(fragmentManager, "contact_us")
        }

        fab.setOnClickListener { updateBox(box) }
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

    class EditNameDialogFragment : DialogFragment() {
        private var mEditText: EditText? = null
        private var mTextView: TextView? = null
        private var mBox: Box? = null

        override fun onCreateDialog(savedInstanceState: Bundle): Dialog {
            val inflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val content = inflater.inflate(R.layout.edit_text_dialog, null)
            mEditText = content.findViewById(R.id.editText)
            mEditText!!.setText(mTextView!!.text)

            val builder = AlertDialog.Builder(activity)
            builder.setTitle("Name")
                    .setView(content)
                    .setPositiveButton("OK") { dialogInterface, i ->
                        val name = mEditText!!.text.toString()
                        mBox!!.name = name
                        mTextView!!.text = name
                    }
                    .setNegativeButton("Cancel") { dialogInterface, i -> }
            return builder.create()
        }

        fun setTextView(textView: TextView) {
            mTextView = textView
        }

        fun setBox(box: Box) {
            mBox = box
        }
    }

    class EditNoticeDialogFragment : DialogFragment() {
        private var mEditText: EditText? = null
        private var mTextView: TextView? = null
        private var mBox: Box? = null

        override fun onCreateDialog(savedInstanceState: Bundle): Dialog {
            val inflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val content = inflater.inflate(R.layout.edit_text_dialog, null)
            mEditText = content.findViewById(R.id.editText)
            mEditText!!.setSingleLine(false)
            mEditText!!.maxLines = 5
            mEditText!!.setText(mTextView!!.text)

            val builder = AlertDialog.Builder(activity)
            builder.setTitle("Notice")
                    .setView(content)
                    .setPositiveButton("OK") { dialogInterface, i ->
                        val notice = mEditText!!.text.toString()
                        mBox!!.notice = notice
                        mTextView!!.text = notice
                    }
                    .setNegativeButton("Cancel") { dialogInterface, i -> }
            return builder.create()
        }

        fun setTextView(textView: TextView) {
            mTextView = textView
        }

        fun setBox(box: Box) {
            mBox = box
        }
    }

    class SharedUsersDialogFragment : DialogFragment() {
        private var mUsers: List<User>? = null

        override fun onCreateDialog(savedInstanceState: Bundle): Dialog {
            val inflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val content = inflater.inflate(R.layout.shared_users_dialog, null)
            val listView = content.findViewById<ListView>(R.id.listView)
            val shareTextView = content.findViewById<TextView>(R.id.shareTextView)

            val adapter = UserListAdapter(activity, mUsers!!)
            listView.adapter = adapter

            shareTextView.setOnClickListener { Log.e("aaa", "share") }

            val builder = AlertDialog.Builder(activity)
                    .setTitle("Shared users")
                    .setView(content)

            return builder.create()
        }

        fun setUsers(users: List<User>?) {
            mUsers = users
        }

        private inner class UserListAdapter(private val mContext: Context, private val mUsers: List<User>) : BaseAdapter() {
            private val mLayoutInflater: LayoutInflater

            init {
                mLayoutInflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            }

            override fun getCount(): Int {
                return mUsers.size
            }

            override fun getItem(i: Int): Any {
                return mUsers[i]
            }

            override fun getItemId(i: Int): Long {
                return i.toLong()
            }

            override fun getView(i: Int, view: View, viewGroup: ViewGroup): View {
                var view = view
                val user = mUsers[i]
                view = mLayoutInflater.inflate(R.layout.shared_user_list_row, viewGroup, false)

                val nameTextView = view.findViewById<TextView>(R.id.shareTextView)
                val mailTextView = view.findViewById<TextView>(R.id.mailTextView)
                val removeImageView = view.findViewById<ImageView>(R.id.removeImageView)

                nameTextView.text = user.name
                mailTextView.text = user.email
                removeImageView.setOnClickListener { Log.e("aaa", "333333333333333333333333333333333333333") }

                return view
            }
        }
    }
}
