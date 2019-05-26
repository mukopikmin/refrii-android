package com.refrii.client.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.refrii.client.R

class NoticeDialogFragment : androidx.fragment.app.DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bundle = arguments
        val inflater = activity?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val name = bundle?.getString("name")
        val notice = bundle?.getString("notice")
        val content = inflater.inflate(R.layout.dialog_notice, null)
        val noticeView = content.findViewById<TextView>(R.id.noticeTextView)

        if (notice.isNullOrBlank()) {
            noticeView.text = "なし"
        } else {
            noticeView.text = notice
        }

        return AlertDialog.Builder(context!!)
                .setTitle("$name のメモ")
                .setView(content)
                .setNegativeButton("閉じる") { _, _ -> }
                .create()
    }

    companion object {
        fun newInstance(name: String, notice: String): NoticeDialogFragment {
            val instance = NoticeDialogFragment()
            val bundle = Bundle()

            bundle.putString("name", name)
            bundle.putString("notice", notice)
            instance.arguments = bundle

            return instance
        }
    }
}