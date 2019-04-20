package com.refrii.client.dialogs

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import java.util.*

class CalendarPickerDialogFragment : androidx.fragment.app.DialogFragment() {

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bundle = arguments
        val time = bundle?.getLong("date")
        val calendar = Calendar.getInstance()
        val date = Date()

        time?.let {
            date.time = it
        }
        calendar.time = date

        return DatePickerDialog(
                activity,
                DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                    val intent = Intent()

                    calendar.set(year, month, dayOfMonth)
                    intent.putExtra("date", calendar.time.time)

                    val pendingIntent = activity?.createPendingResult(targetRequestCode, intent, PendingIntent.FLAG_ONE_SHOT)
                    pendingIntent?.send(Activity.RESULT_OK)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH))
    }

    companion object {
        fun newInstance(date: Date): CalendarPickerDialogFragment {
            val instance = CalendarPickerDialogFragment()
            val bundle = Bundle()

            bundle.putLong("date", date.time)
            instance.arguments = bundle

            return instance
        }
    }
}