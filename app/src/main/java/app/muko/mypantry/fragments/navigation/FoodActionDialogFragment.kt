package app.muko.mypantry.fragments.navigation

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import app.muko.mypantry.R
import app.muko.mypantry.data.models.Food
import app.muko.mypantry.di.ViewModelFactory
import app.muko.mypantry.food.FoodActivity
import app.muko.mypantry.fragments.foodlist.FoodListViewModel
import app.muko.mypantry.noticelist.NoticeListActivity
import butterknife.BindView
import butterknife.ButterKnife
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class FoodActionDialogFragment : BottomSheetDialogFragment() {

    @BindView(R.id.navigationFoodNameTextView)
    lateinit var nameView: TextView

    @BindView(R.id.navigationFoodAmountTextView)
    lateinit var amountView: TextView

    @BindView(R.id.navigationIncreasetButton)
    lateinit var increaseButton: Button

    @BindView(R.id.navigationDecreaseButton)
    lateinit var decreaseButton: Button

    @BindView(R.id.navigationNoticeButton)
    lateinit var noticeButton: ImageButton

    @BindView(R.id.navigationEditButton)
    lateinit var editButton: ImageButton

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any>

    private lateinit var viewModel: FoodActionDialogViewModel

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_item_list_dialog_list_dialog, container, false)

        ButterKnife.bind(this, view)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val args = arguments ?: return
        val foodId = args.getInt(ARG_FOOD_ID)

        viewModel = ViewModelProvider(this, viewModelFactory).get(FoodActionDialogViewModel::class.java)
        viewModel.initFood(foodId)

        increaseButton.setOnClickListener { viewModel.incrementFood() }
        decreaseButton.setOnClickListener { viewModel.decrementFood() }
        noticeButton.setOnClickListener {showNotices(args.getInt(ARG_FOOD_ID))}
        editButton.setOnClickListener{editFood(args.getInt(ARG_FOOD_ID))}

        viewModel.food.observe(viewLifecycleOwner, Observer {
            this.setFood(it)
        })
    }

    private fun setFood(food: Food) {
        nameView.text = food.name
        amountView.text = "${food.amount} ${food.unit.label}"
        increaseButton.text = food.unit.step.toString()
        decreaseButton.text = food.unit.step.toString()
    }

    private fun showNotices(foodId: Int) {
        val intent = Intent(activity, NoticeListActivity::class.java)

        intent.putExtra(getString(R.string.key_food_id), foodId)
        startActivity(intent)
    }

    private fun editFood(foodId:Int) {
        val intent = Intent(activity, FoodActivity::class.java)

        intent.putExtra(getString(R.string.key_food_id), foodId)
        startActivity(intent)
    }

    companion object {
        private const val ARG_FOOD_ID = "food_id"

        fun newInstance(food: Food): FoodActionDialogFragment =
                FoodActionDialogFragment().apply {
                    arguments = Bundle().apply {
                        putInt(ARG_FOOD_ID, food.id)
                    }
                }
    }
}