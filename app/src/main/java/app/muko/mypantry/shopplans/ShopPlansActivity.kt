package app.muko.mypantry.shopplans

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.muko.mypantry.App
import app.muko.mypantry.R
import app.muko.mypantry.data.models.ShopPlan
import butterknife.BindView
import butterknife.ButterKnife
import com.google.android.material.snackbar.Snackbar
import dagger.android.AndroidInjection
import javax.inject.Inject

class ShopPlansActivity : AppCompatActivity(), ShopPlansContract.View {

    @BindView(R.id.toolbar)
    lateinit var toolbar: Toolbar

    @BindView(R.id.recyclerView)
    lateinit var shopPlanListRecycler: RecyclerView

    @BindView(R.id.progressBar)
    lateinit var progressBar: ProgressBar

    @Inject
    lateinit var presenter: ShopPlansPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AndroidInjection.inject(this)
//        (application as App).getComponent().inject(this)
        setContentView(R.layout.activity_shop_plans)
        ButterKnife.bind(this)
        setSupportActionBar(toolbar)

        title = "買い物の予定"

        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeButtonEnabled(true)
        }

        shopPlanListRecycler.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        shopPlanListRecycler.layoutManager = LinearLayoutManager(this)
    }

    override fun onStart() {
        super.onStart()

        presenter.init(this)
        presenter.getShopPlans()

        progressBar.visibility = View.GONE
    }

    override fun setShopPlans(shopPlans: List<ShopPlan>) {
        val donePlans = shopPlans.filterNot { it.done }

        if (shopPlanListRecycler.adapter == null) {
            val adapter = ShopPlansRecyclerViewAdapter(donePlans)

            shopPlanListRecycler.adapter = adapter
        } else {
            val adapter = shopPlanListRecycler.adapter as ShopPlansRecyclerViewAdapter

            adapter.setShopPlans(donePlans)
            adapter.setOnClickListener(View.OnClickListener {
                val position = shopPlanListRecycler.getChildAdapterPosition(it)
                val plan = adapter.getItemAtPosition(position)

                presenter.completeShopPlan(plan)
            })
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

    override fun showToast(message: String?) {
        message ?: return

        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun showSnackBar(message: String?) {
        message ?: return

        Snackbar.make(shopPlanListRecycler, message, Snackbar.LENGTH_SHORT).show()
    }
}