package com.refrii.client.views.fragments

import android.app.Activity
import android.app.Dialog
import android.app.DialogFragment
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog

class OptionsPickerDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bundle = arguments
        val title = bundle.getString("title")
        val options = bundle.getStringArray("options")
        val foodId = bundle.getInt("food_id", 0)

        return AlertDialog.Builder(activity)
                .setTitle(title)
                .setItems(options) { _, which ->
                    val intent = Intent()
                    intent.putExtra("option", which)
                    intent.putExtra("food_id", foodId)

                    val pendingIntent = activity.createPendingResult(targetRequestCode, intent, PendingIntent.FLAG_ONE_SHOT)
                    pendingIntent.send(Activity.RESULT_OK)
                }
                .create()
    }

    companion object {
        fun newInstance(title: String, options: Array<String>, foodId: Int): OptionsPickerDialogFragment {
            val instance = OptionsPickerDialogFragment()
            val bundle = Bundle()

            bundle.putString("title", title)
            bundle.putStringArray("options", options)
            bundle.putInt("food_id", foodId)
            instance.arguments = bundle

            return instance
        }
    }
}