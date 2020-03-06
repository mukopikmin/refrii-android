package app.muko.mypantry.dialogs

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
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import app.muko.mypantry.R
import app.muko.mypantry.data.models.User

class InviteUserDialogFragment : androidx.fragment.app.DialogFragment() {

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = activity?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val content = inflater.inflate(R.layout.dialog_invite_user, null)
        val editText = content.findViewById<EditText>(R.id.editText)
        val message = content.findViewById<TextView>(R.id.messageTextView)
        val bundle = arguments
        val sharedUsersEmail = bundle?.getStringArrayList("shared_users_email")
        val dialog = AlertDialog.Builder(activity!!)
                .setTitle("カテゴリの共有")
                .setView(content)
                .setPositiveButton(getString(R.string.message_share_box)) { dialog, which ->
                    val intent = Intent()
                            .putExtra("email", editText.text.toString())
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

                if (sharedUsersEmail != null && sharedUsersEmail.contains(s.toString())) {
                    button.isEnabled = false
                    message.visibility = View.VISIBLE
                    message.text = "すでに共有されています"
                } else {
                    button.isEnabled = true
                    message.visibility = View.GONE
                    message.text = ""
                }
            }
        }

        message.visibility = View.GONE
        editText.addTextChangedListener(textWatcher)

        return dialog
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