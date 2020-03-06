package app.muko.mypantry.dialogs

import android.app.Activity
import android.app.Dialog
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog

class OptionsPickerDialogFragment : androidx.fragment.app.DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bundle = arguments
        val title = bundle?.getString("title")
        val options = bundle?.getStringArray("options")
        val targetId = bundle?.getInt("target_id")
        val dialogBuilder = AlertDialog.Builder(activity!!)

        title?.let { dialogBuilder.setTitle(title) }
        dialogBuilder.setItems(options) { _, which ->
            val intent = Intent()
            intent.putExtra("option", which)
            intent.putExtra("target_id", targetId)

            val pendingIntent = activity?.createPendingResult(targetRequestCode, intent, PendingIntent.FLAG_ONE_SHOT)
            pendingIntent?.send(Activity.RESULT_OK)
        }

        return dialogBuilder.create()
    }

    companion object {
        fun newInstance(title: String?, options: Array<String>, targetId: Int?): OptionsPickerDialogFragment {
            val instance = OptionsPickerDialogFragment()
            val bundle = Bundle()

            bundle.putString("title", title)
            bundle.putStringArray("options", options)
            targetId?.let { bundle.putInt("target_id", it) }
            instance.arguments = bundle

            return instance
        }
    }
}