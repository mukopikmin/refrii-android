package com.refrii.client.shopplans

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.refrii.client.App
import com.refrii.client.R
import com.refrii.client.data.models.ShopPlan
import javax.inject.Inject

class ShopPlansActivity : AppCompatActivity(), ShopPlansContract.View {

    @BindView(R.id.toolbar)
    lateinit var mToolbar: Toolbar
    @BindView(R.id.recyclerView)
    lateinit var mRecyclerView: RecyclerView
    @BindView(R.id.progressBar)
    lateinit var mProgressBar: ProgressBar

    @Inject
    lateinit var mPresenter: ShopPlansPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (application as App).getComponent().inject(this)
        setContentView(R.layout.activity_shop_plans)
        ButterKnife.bind(this)
        setSupportActionBar(mToolbar)

        title = "買い物の予定"

        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeButtonEnabled(true)
        }

        mRecyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        mRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    override fun onStart() {
        super.onStart()

        mPresenter.takeView(this)
        mPresenter.getShopPlans()

        mProgressBar.visibility = View.GONE
    }

    override fun setShopPlans(shopPlans: List<ShopPlan>?) {
        shopPlans?.let {
            if (mRecyclerView.adapter == null) {
                val adapter = ShopPlansRecyclerViewAdapter(it)

                mRecyclerView.adapter = adapter
            } else {
                val adapter = mRecyclerView.adapter as ShopPlansRecyclerViewAdapter

                adapter.setShopPlans(it)
                adapter.setOnClickListener(View.OnClickListener {
                    val position = mRecyclerView.getChildAdapterPosition(it)
                    val plan = adapter.getItemAtPosition(position)

                    mPresenter.completeShopPlan(plan)
                })
            }
        }
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
}