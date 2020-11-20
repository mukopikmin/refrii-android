package app.muko.mypantry.ui.fragments.expiring

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import app.muko.mypantry.R
import app.muko.mypantry.data.models.Food
import app.muko.mypantry.di.ViewModelFactory
import app.muko.mypantry.ui.activities.foodlist.FoodListActivity
import app.muko.mypantry.ui.fragments.foodaction.FoodActionDialogFragment
import app.muko.mypantry.ui.fragments.foodlist.FoodRecyclerViewAdapter
import butterknife.BindView
import butterknife.ButterKnife
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.android.support.DaggerFragment
import java.util.*
import javax.inject.Inject

class ExpiringFoodsFragment : DaggerFragment(), SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.recyclerView)
    lateinit var recyclerView: RecyclerView

    @BindView(R.id.addFoodFab)
    lateinit var fab: FloatingActionButton

    @BindView(R.id.swipeRefreshLayout)
    lateinit var swipeRefreshLayout: SwipeRefreshLayout

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var preference: SharedPreferences

    private lateinit var viewModel: ExpiringFoodsViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_food_list, container, false)

        ButterKnife.bind(this, view)

        recyclerView.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))
        recyclerView.layoutManager = LinearLayoutManager(activity)
        fab.visibility = View.GONE
        swipeRefreshLayout.setOnRefreshListener(this)

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProvider(this, viewModelFactory).get(ExpiringFoodsViewModel::class.java)
        viewModel.getFoods()
        (activity as FoodListActivity).setActionBar("期限が1週間以内")

        viewModel.foods.observe(viewLifecycleOwner, Observer { foods ->
            val expiringFoods = foods.filter { Date().time - it.expirationDate.time < EXPIRING_DAYS }

            if (expiringFoods.isEmpty()) {
                (activity as FoodListActivity).setEmptyBoxMessage()
            } else {
                setFoods(expiringFoods)
            }

            swipeRefreshLayout.isRefreshing = false
        })

        viewModel.selectedFood.observe(viewLifecycleOwner, Observer {
            (recyclerView.adapter as FoodRecyclerViewAdapter).select(it)
        })
    }

    override fun onRefresh() {
        viewModel.getFoods()
    }

    private fun setFoods(foods: List<Food>) {
        val userId = preference.getInt(getString(R.string.preference_key_id), -1)

        if (recyclerView.adapter == null) {
            val adapter = FoodRecyclerViewAdapter(foods, userId)

            adapter.setOnClickListener(View.OnClickListener { view ->
                val position = recyclerView.getChildAdapterPosition(view)
                val food = adapter.getItemAtPosition(position)

                food?.let {
                    val fragment = FoodActionDialogFragment.newInstance(food)

                    activity?.supportFragmentManager?.let {
                        fragment.show(it, fragment.tag)
                    }
                }
            })

            recyclerView.adapter = adapter
        } else {
            val adapter = recyclerView.adapter as FoodRecyclerViewAdapter

            adapter.setFoods(foods)
        }
    }

    companion object {

        private const val EXPIRING_DAYS = 7 * 24 * 60 * 1000

        @JvmStatic
        fun newInstance() = ExpiringFoodsFragment()
    }
}