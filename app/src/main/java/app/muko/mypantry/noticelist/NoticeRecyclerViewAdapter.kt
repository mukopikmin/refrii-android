package app.muko.mypantry.noticelist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.muko.mypantry.R
import app.muko.mypantry.data.models.Notice

class NoticeRecyclerViewAdapter(
        private var mNotices: List<Notice>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mOnLongClickListener: View.OnLongClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_notice, parent, false)

        return NoticeViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mNotices.size
    }

    override fun onBindViewHolder(_holder: RecyclerView.ViewHolder, position: Int) {
        val holder = _holder as NoticeViewHolder
        val notice = mNotices[position]

        mOnLongClickListener?.let {
            holder.bind(notice, it)
        }
    }

    fun setNotices(notices: List<Notice>) {
        mNotices = notices

        notifyDataSetChanged()
    }

    fun setOnLongClickListener(listener: View.OnLongClickListener) {
        mOnLongClickListener = listener
    }

    fun getItemAt(position: Int): Notice {
        return mNotices[position]
    }
}