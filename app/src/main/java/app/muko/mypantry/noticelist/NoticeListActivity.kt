package app.muko.mypantry.noticelist

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.muko.mypantry.App
import app.muko.mypantry.R
import app.muko.mypantry.data.models.Food
import app.muko.mypantry.data.models.Notice
import app.muko.mypantry.dialogs.ConfirmDialogFragment
import butterknife.BindView
import butterknife.ButterKnife
import javax.inject.Inject

class NoticeListActivity : AppCompatActivity(), NoticeListContract.View {

    @BindView(R.id.recyclerView)
    lateinit var mRecyclerView: RecyclerView

    @BindView(R.id.noticeEditText)
    lateinit var mNoticeEditText: EditText

    @BindView(R.id.button)
    lateinit var mButton: Button

    @Inject
    lateinit var mPresenter: NoticeListPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (application as App).getComponent().inject(this)
        setContentView(R.layout.activity_notice_list)
        ButterKnife.bind(this)

        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeButtonEnabled(true)
        }

        mRecyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        mRecyclerView.layoutManager = LinearLayoutManager(this)

        mButton.setOnClickListener { createNotice() }
    }

    override fun onStart() {
        super.onStart()

        val foodId = intent.getIntExtra(getString(R.string.key_food_id), -1)

        mPresenter.init(this, foodId)
        mPresenter.getFood(foodId)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        var result = true

        when (id) {
            android.R.id.home -> finish()
            else -> result = super.onOptionsItemSelected(item)
        }

        return result
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK || data == null) return

        when (requestCode) {
            REMOVE_NOTICE_REQUEST_CODE -> mPresenter.removeNotice()
        }
    }

    private fun createNotice() {
        val text = mNoticeEditText.text.toString()

        if (text.isNotBlank()) {
            mPresenter.createNotice(text)
        }
    }

    override fun setFood(food: Food) {
        title = food.name
    }

    override fun setNotices(notices: List<Notice>) {
        if (mRecyclerView.adapter == null) {
            val adapter = NoticeRecyclerViewAdapter(notices)

            adapter.setOnLongClickListener(View.OnLongClickListener {
                val position = mRecyclerView.getChildAdapterPosition(it)
                val notice = adapter.getItemAt(position)

                mPresenter.confirmRemovingNotice(notice)

                true
            })

            mRecyclerView.adapter = adapter
            mRecyclerView.scrollToPosition(adapter.itemCount - 1)
        } else {
            val adapter = mRecyclerView.adapter as NoticeRecyclerViewAdapter

            adapter.setNotices(notices)
            mRecyclerView.scrollToPosition(adapter.itemCount - 1)
        }
    }

    override fun resetForm() {
        mNoticeEditText.text = null
    }

    override fun onRemoveCompleted() {
        val foodId = intent.getIntExtra(getString(R.string.key_food_id), -1)

        mPresenter.getFood(foodId)
    }

    override fun showRemoveConfirmation(title: String, notice: Notice) {
        val fragment = ConfirmDialogFragment.newInstance(title, "削除していいですか？", notice.id)

        fragment.setTargetFragment(null, REMOVE_NOTICE_REQUEST_CODE)
        fragment.show(supportFragmentManager, "delete_food")
    }

    override fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    companion object {
        private const val REMOVE_NOTICE_REQUEST_CODE = 101
    }
}
