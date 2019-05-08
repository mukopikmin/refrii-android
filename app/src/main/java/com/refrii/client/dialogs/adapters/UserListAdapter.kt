package com.refrii.client.dialogs.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.refrii.client.R

class UserListAdapter(
        mContext: Context,
        private val names: List<String>,
        private val emails: List<String>) : BaseAdapter() {

    private val mLayoutInflater: LayoutInflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return names.size
    }

    override fun getItem(i: Int): Any {
        return names[i]
    }

    override fun getItemId(i: Int): Long {
        return i.toLong()
    }

    @SuppressLint("ViewHolder")
    override fun getView(i: Int, _view: View?, viewGroup: ViewGroup): View {
//        var view = view
        val name = names[i]
        val email = emails[i]
        val view = mLayoutInflater.inflate(R.layout.shared_user_list_row, viewGroup, false)

        val nameTextView = view.findViewById<TextView>(R.id.foodNameTextView)
        val mailTextView = view.findViewById<TextView>(R.id.mailTextView)
        val removeImageView = view.findViewById<ImageView>(R.id.removeImageView)

        nameTextView.text = name
        mailTextView.text = email
        removeImageView.setOnClickListener { Log.e("aaa", "333333333333333333333333333333333333333") }

        return view
    }
}