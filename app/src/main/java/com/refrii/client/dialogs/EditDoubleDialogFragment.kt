package com.refrii.client.dialogs

import android.app.Activity
import android.app.Dialog
import android.app.DialogFragment
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.refrii.client.R

class EditDoubleDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val content = inflater.inflate(R.layout.edit_text_dialog, null)
        val editText = content.findViewById<EditText>(R.id.editText)
        val bundle = arguments
        val title = bundle.getString("title")
        val number = bundle.getDouble("number")

        editText.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        editText.setText(number.toString())

        return AlertDialog.Builder(activity)
                .setTitle(title)
                .setView(content)
                .setPositiveButton("OK") { _, _ ->
                    val intent = Intent()
                    intent.putExtra("number", editText.text.toString().toDouble())
                    val pendingIntent = activity.createPendingResult(targetRequestCode, intent, PendingIntent.FLAG_ONE_SHOT)
                    pendingIntent.send(Activity.RESULT_OK)
                }
                .setNegativeButton("Cancel") { _, _ -> }
                .create()
    }

    companion object {
        fun newInstance(title: String, number: Double): EditDoubleDialogFragment {
            val instance = EditDoubleDialogFragment()
            val bundle = Bundle()

            bundle.putString("title", title)
            bundle.putDouble("number", number)
            instance.arguments = bundle

            return instance
        }
    }
}