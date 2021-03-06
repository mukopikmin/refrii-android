package app.muko.mypantry.noticelist

import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import app.muko.mypantry.R
import app.muko.mypantry.data.models.Notice
import butterknife.BindView
import butterknife.ButterKnife
import java.text.SimpleDateFormat
import java.util.*

class NoticeViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    @BindView(R.id.constraintLayout)
    lateinit var constraintLayout: ConstraintLayout
    @BindView(R.id.textTextView)
    lateinit var textTextView: TextView
    @BindView(R.id.timestampTextView)
    lateinit var timestampTextView: TextView

    init {
        ButterKnife.bind(this, view)
    }

    fun bind(notice: Notice, listener: View.OnLongClickListener) {
        val context = textTextView.context
        val formatter = SimpleDateFormat(context.getString(R.string.format_datetime), Locale.getDefault())

        textTextView.text = notice.text
        timestampTextView.text = formatter.format(notice.createdAt)
        constraintLayout.setOnLongClickListener { listener.onLongClick(it) }
    }
}