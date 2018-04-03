package com.refrii.client.dialogs

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.app.DialogFragment
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.widget.DatePicker
import com.refrii.client.R
import java.util.*

class CalendarPickerDialogFragment : DialogFragment() {

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val content = inflater.inflate(R.layout.edit_date_dialog, null)
        val datePicker = content.findViewById<DatePicker>(R.id.datePicker)
        val bundle = arguments
        val time = bundle.getLong("date")
        val calendar = Calendar.getInstance()
        val date = Date()

        date.time = time
        calendar.time = date
        datePicker.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))

        return AlertDialog.Builder(activity)
                .setView(content)
                .setPositiveButton("OK") { _, _ ->
                    val intent = Intent()

                    calendar.set(datePicker.year, datePicker.month, datePicker.dayOfMonth)
                    intent.putExtra("date", calendar.time.time)

                    val pendingIntent = activity.createPendingResult(targetRequestCode, intent, PendingIntent.FLAG_ONE_SHOT)
                    pendingIntent.send(Activity.RESULT_OK)
                }
                .setNegativeButton("Cancel") { _, _ -> }
                .create()
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