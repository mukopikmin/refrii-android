package app.muko.mypantry.fragments.foodlist

import android.content.Intent
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
import app.muko.mypantry.R
import app.muko.mypantry.data.models.Box
import app.muko.mypantry.data.models.Food
import app.muko.mypantry.di.ViewModelFactory
import app.muko.mypantry.food.FoodActivity
import app.muko.mypantry.foodlist.FoodListActivity
import app.muko.mypantry.fragments.navigation.FoodActionDialogFragment
import butterknife.BindView
import butterknife.ButterKnife
import dagger.android.HasAndroidInjector
import dagger.android.support.DaggerFragment
import javax.inject.Inject

class FoodListFragment : DaggerFragment(), HasAndroidInjector {

    companion object {
        private const val ADD_BOX_REQUEST_CODE = 101
        private const val ADD_FOOD_REQUEST_CODE = 102
        private const val EDIT_FOOD_REQUEST_CODE = 103
        private const val REMOVE_FOOD_REQUEST_CODE = 104
        private const val REMOVE_BOX_REQUEST_CODE = 105
        private const val CREATE_BOX_REQUEST_CODE = 106
        private const val SHOW_NOTICE_REQUEST_CODE = 107

        fun newInstance(boxId: Int): FoodListFragment {
            val fragment = FoodListFragment()
            val args = Bundle()

            args.putInt("box_id", boxId)
            fragment.arguments = args

            return fragment
        }
    }

    @BindView(R.id.recyclerView)
    lateinit var recyclerView: RecyclerView

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var preference: SharedPreferences

    private lateinit var viewModel: FoodListViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_food_list, container, false)

        ButterKnife.bind(this, view)

        recyclerView.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))
        recyclerView.layoutManager = LinearLayoutManager(activity)

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val args = arguments
        val boxId = args?.getInt("box_id") ?: return

        viewModel = ViewModelProvider(this, viewModelFactory).get(FoodListViewModel::class.java)
        viewModel.initBox(boxId)
        viewModel.getFoods()

        viewModel.box.observe(viewLifecycleOwner, Observer {
            (activity as FoodListActivity).setActionBar(it.name)
        })

        viewModel.foods.observe(viewLifecycleOwner, Observer { foods ->
            val foodsInBox = foods.filter { it.box.id == boxId }
            val box = viewModel.box.value ?: return@Observer

            if (foodsInBox.isEmpty()) {
                (activity as FoodListActivity).setEmptyBoxMessage()
            } else {
                setFoods(box.name, foodsInBox)
            }
        })

        viewModel.selectedFood.observe(viewLifecycleOwner, Observer {
            val adapter =recyclerView.adapter as FoodRecyclerViewAdapter

            adapter.select(it)
        })
    }

    private fun setFoods(boxName: String, foods: List<Food>) {
        val userId = preference.getInt(getString(R.string.preference_key_id), -1)

        if (recyclerView.adapter == null) {
            val adapter = FoodRecyclerViewAdapter(foods, userId)

            adapter.setOnClickListener(View.OnClickListener { view ->
                val position = recyclerView.getChildAdapterPosition(view)
                val food = adapter.getItemAtPosition(position)
//
//                food?.id?.let {
//                    showFood(it, null)
//                }

                food?.let{
                val bottomSheetFragment = FoodActionDialogFragment.newInstance(it)
                bottomSheetFragment?.show(activity!!.supportFragmentManager, bottomSheetFragment?.tag)
                }

//                viewModel.selectFood(food)
            })

            recyclerView.adapter = adapter
        } else {
            val adapter = recyclerView.adapter as FoodRecyclerViewAdapter

            adapter.setFoods(foods)
        }
    }

    fun showFood(id: Int, box: Box?) {
        val intent = Intent(context, FoodActivity::class.java)

        intent.putExtra(getString(R.string.key_food_id), id)
        intent.putExtra(getString(R.string.key_box_id), box?.id)

        startActivityForResult(intent, EDIT_FOOD_REQUEST_CODE)
    }
}