package com.refrii.client.dialogs

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.widget.EditText
import com.refrii.client.R
import com.refrii.client.data.api.models.User

class InviteUserDialogFragment : DialogFragment() {

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = activity?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val content = inflater.inflate(R.layout.invite_user_dialog, null)
        val editText = content.findViewById<EditText>(R.id.editText)
        val bundle = arguments
        val sharedUsersEmail = bundle?.getStringArrayList("shared_users_email")

        return AlertDialog.Builder(activity!!)
                .setTitle("カテゴリの共有")
                .setView(content)
                .setPositiveButton(getString(R.string.message_share_box)) { _, _ ->
                    val intent = Intent()
                            .putExtra("email", editText.text.toString())
                    val pendingIntent = activity?.createPendingResult(targetRequestCode, intent, PendingIntent.FLAG_ONE_SHOT)

                    pendingIntent?.send(Activity.RESULT_OK)
                }
                .setNegativeButton(getString(R.string.message_cancel)) { _, _ -> }
                .create()
    }

    companion object {
        fun newInstance(sharedUsers: List<User>): InviteUserDialogFragment {
            val instance = InviteUserDialogFragment()
            val bundle = Bundle()
            val emails = sharedUsers.map { it.email } as ArrayList

            bundle.putStringArrayList("shared_users_email", emails)
            instance.arguments = bundle

            return instance
        }
    }
}