package com.refrii.client.dialogs

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.DialogFragment
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.refrii.client.R
import com.refrii.client.data.api.models.User
import com.refrii.client.dialogs.adapters.UserListAdapter

class UserPickerDialogFragment : DialogFragment() {

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val activity = activity
        val inflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val content = inflater.inflate(R.layout.shared_users_dialog, null)
        val bundle = arguments
        val title = bundle.getString("title")
        val names = bundle.getStringArrayList("names")
        val emails = bundle.getStringArrayList("emails")
        val listView = content.findViewById<ListView>(R.id.listView)
        val shareTextView = content.findViewById<TextView>(R.id.nameTextView)
        val adapter = UserListAdapter(activity, names, emails)

        listView.adapter = adapter
        shareTextView.setOnClickListener { Log.e(TAG, "share") }

        return AlertDialog.Builder(activity)
                .setTitle(title)
                .setView(content)
                .setNegativeButton(getString(R.string.message_cancel)) { _, _ -> }
                .create()
    }

    companion object {
        private const val TAG = "UserPickerDialog"

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