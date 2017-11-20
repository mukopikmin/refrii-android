package com.refrii.client.views.fragments

import android.app.Dialog
import android.app.DialogFragment
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import com.refrii.client.R
import com.refrii.client.models.User
import com.refrii.client.views.adapters.UserListAdapter

class UserPickerDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val activity = activity
        val inflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val content = inflater.inflate(R.layout.shared_users_dialog, null)
        val bundle = arguments
        val title = bundle.getString("title")
        val names = bundle.getStringArrayList("names")
        val emails = bundle.getStringArrayList("emails")
        val listView = content.findViewById<ListView>(R.id.listView)
        val shareTextView = content.findViewById<TextView>(R.id.shareTextView)

        val adapter = UserListAdapter(activity, names, emails)
        listView.adapter = adapter

        shareTextView.setOnClickListener { Log.e("aaa", "share") }

        return AlertDialog.Builder(activity)
                .setTitle(title)
                .setView(content)
                .create()
    }

    companion object {
        fun newInstance(title: String, users: List<User>): UserPickerDialogFragment {
            val instance = UserPickerDialogFragment()
            val bundle = Bundle()
            val names = ArrayList<String>(users.map { it.name!! })
            val emails = ArrayList<String>(users.map { it.email!! })

            bundle.putString("title", title)
            bundle.putStringArrayList("names", names)
            bundle.putStringArrayList("emails", emails)

            instance.arguments = bundle

            return instance
        }
    }
}