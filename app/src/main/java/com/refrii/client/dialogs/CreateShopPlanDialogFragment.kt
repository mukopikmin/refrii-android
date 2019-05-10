package com.refrii.client.dialogs

import android.app.Activity
import android.app.Dialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.CalendarView
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.refrii.client.R

class CreateShopPlanDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bundle = arguments
        val inflater = activity?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val content = inflater.inflate(R.layout.dialog_create_shop_plan, null)
        val unitLabelView: TextView = content.findViewById(R.id.unitTextView)
        val amountView: EditText = content.findViewById(R.id.amountEditText)
        val calendarView: CalendarView = content.findViewById(R.id.calendarView)
        val unitLabel = bundle?.getString("unit_label")

        unitLabelView.text = unitLabel

        return AlertDialog.Builder(context!!)
                .setTitle("買い物の予定を作成")
                .setView(content)
                .setPositiveButton("作成") { _, _ ->
                    val intent = Intent()
                    val amount = amountView.text.toString().toDouble()
                    val date = calendarView.date

                    intent.putExtra("key_amount", amount)
                    intent.putExtra("key_date", date)

                    activity?.createPendingResult(targetRequestCode, intent, PendingIntent.FLAG_ONE_SHOT)
                            ?.send(Activity.RESULT_OK)
                }
                .setNegativeButton("キャンセル") { _, _ -> }
                .create()
    }

    companion object {
        fun newInstance(unitLabel: String): CreateShopPlanDialogFragment {
            val instance = CreateShopPlanDialogFragment()
            val bundle = Bundle()

            bundle.putString("unit_label", unitLabel)
            instance.arguments = bundle

            return instance
        }
    }
}