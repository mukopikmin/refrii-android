package com.refrii.client.dialogs

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.refrii.client.R

class CreateBoxDialogFragment : androidx.fragment.app.DialogFragment() {

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = activity?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val content = inflater.inflate(R.layout.create_box_dialog, null)
        val editText = content.findViewById<EditText>(R.id.editText)
        val dialog = AlertDialog.Builder(activity!!)
                .setTitle("カテゴリの作成")
                .setView(content)
                .setPositiveButton("作成") { dialog, which ->
                    val intent = Intent()
                            .putExtra("name", editText.text.toString())
                    val pendingIntent = activity?.createPendingResult(targetRequestCode, intent, PendingIntent.FLAG_ONE_SHOT)

                    pendingIntent?.send(Activity.RESULT_OK)
                }
                .setNegativeButton(getString(R.string.message_cancel)) { _, _ -> }
                .create()
        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val button = dialog.getButton(AlertDialog.BUTTON_POSITIVE)

                s?.let {
                    button.isEnabled = it.isNotBlank()
                }
            }
        }

        editText.addTextChangedListener(textWatcher)
        dialog.setOnShowListener {
            val button = dialog.getButton(AlertDialog.BUTTON_POSITIVE)

            button.isEnabled = false
        }

        return dialog
    }

    companion object {
        fun newInstance(): CreateBoxDialogFragment {
            return CreateBoxDialogFragment()
        }
    }
}