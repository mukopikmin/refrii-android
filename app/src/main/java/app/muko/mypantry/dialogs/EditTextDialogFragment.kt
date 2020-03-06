package app.muko.mypantry.dialogs

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.app.DialogFragment
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import app.muko.mypantry.R

class EditTextDialogFragment : DialogFragment() {

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val content = inflater.inflate(R.layout.dialog_edit_text, null)
        val editText = content.findViewById<EditText>(R.id.editText)
        val bundle = arguments
        val title = bundle.getString("title")
        val text = bundle.getString("text")
        val multilineEnabled = bundle.getBoolean("multiline_enabled")

        editText.setText(text)
        if (multilineEnabled) {
            editText.setSingleLine(false)
            editText.maxLines = MULTILINE_LENGTH
        }

        return AlertDialog.Builder(activity)
                .setTitle(title)
                .setView(content)
                .setPositiveButton("OK") { _, _ ->
                    val intent = Intent()
                    intent.putExtra("text", editText.text.toString())

                    val pendingIntent = activity.createPendingResult(targetRequestCode, intent, PendingIntent.FLAG_ONE_SHOT)
                    pendingIntent.send(Activity.RESULT_OK)
                }
                .setNegativeButton("Cancel") { _, _ -> }
                .create()
    }

    companion object {
        private val MULTILINE_LENGTH = 5

        fun newInstance(title: String, text: String, multilineEnabled: Boolean = false): EditTextDialogFragment {
            val instance = EditTextDialogFragment()
            val bundle = Bundle()

            bundle.putString("title", title)
            bundle.putString("text", text)
            bundle.putBoolean("multiline_enabled", multilineEnabled)
            instance.arguments = bundle

            return instance
        }
    }
}