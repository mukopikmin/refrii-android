package com.refrii.client.dialogs

import android.app.Activity
import android.app.Dialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.refrii.client.R

class ConfirmDialogFragment : androidx.fragment.app.DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bundle = arguments
        val inflater = activity?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val title = bundle?.getString("title")
        val message = bundle?.getString("message")
        val content = inflater.inflate(R.layout.confirm_dialog, null)
        val messageText = content.findViewById<TextView>(R.id.messageText)
        val targetId = bundle?.getInt("target_id", 0)

        messageText.text = message

        return AlertDialog.Builder(context!!)
                .setTitle(title)
                .setView(content)
                .setPositiveButton(getString(R.string.message_yes)) { _, _ ->
                    val intent = Intent()
                    intent.putExtra("target_id", targetId)

                    val pendingIntent = activity?.createPendingResult(targetRequestCode, intent, PendingIntent.FLAG_ONE_SHOT)
                    pendingIntent?.send(Activity.RESULT_OK)
                }
                .setNegativeButton(getString(R.string.message_no)) { _, _ -> }
                .create()
    }

    companion object {
        fun newInstance(title: String, message: String, targetId: Int): ConfirmDialogFragment {
            val instance = ConfirmDialogFragment()
            val bundle = Bundle()

            bundle.putString("title", title)
            bundle.putString("message", message)
            bundle.putInt("target_id", targetId)
            instance.arguments = bundle

            return instance
        }
    }
}