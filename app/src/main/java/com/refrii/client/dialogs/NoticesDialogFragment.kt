package com.refrii.client.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.refrii.client.R
import com.refrii.client.data.models.Notice
import com.refrii.client.dialogs.adapters.NoticeRecyclerViewAdapter
import java.util.*

class NoticesDialogFragment : androidx.fragment.app.DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bundle = arguments
        val inflater = activity?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val name = bundle?.getString("name")
        val texts = bundle?.getStringArrayList("texts")
        val timestamps = bundle?.getLongArray("timestamps")
        val content = inflater.inflate(R.layout.dialog_notices, null)
        val recyclerView = content.findViewById<RecyclerView>(R.id.recyclerView)

        texts?.let {
            timestamps?.let {
                val dateList = timestamps.map { Date(it) }
                val adapter = NoticeRecyclerViewAdapter(texts, dateList)

                recyclerView.adapter = adapter
                recyclerView.layoutManager = LinearLayoutManager(activity)
            }
        }

        return AlertDialog.Builder(context!!)
                .setTitle("$name のメモ")
                .setView(content)
                .setNegativeButton("閉じる") { _, _ -> }
                .create()
    }

    companion object {
        fun newInstance(name: String, notices: List<Notice>): NoticesDialogFragment {
            val instance = NoticesDialogFragment()
            val bundle = Bundle()
            val texts = notices.map { it.text } as ArrayList
            val timestamps = notices.map { it.createdAt!!.time }.toLongArray()

            bundle.putString("name", name)
            bundle.putStringArrayList("texts", texts)
            bundle.putLongArray("timestamps", timestamps)

            instance.arguments = bundle

            return instance
        }
    }
}