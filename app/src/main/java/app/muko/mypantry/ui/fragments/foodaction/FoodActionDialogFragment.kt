package app.muko.mypantry.ui.fragments.foodaction

import android.content.Context
import android.content.Intent
import android.os.Bundle
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
import app.muko.mypantry.dialogs.ConfirmDialogFragment
import app.muko.mypantry.noticelist.NoticeListActivity
import app.muko.mypantry.ui.activities.food.FoodActivity
import app.muko.mypantry.ui.activities.foodlist.FoodListActivity
import butterknife.BindView
import butterknife.ButterKnife
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.android.DispatchingAndroidInjector
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

    @BindView(R.id.navigationDeleteButton)
    lateinit var deleteButton: ImageButton

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
        noticeButton.setOnClickListener { showNotices(args.getInt(ARG_FOOD_ID)) }
        editButton.setOnClickListener { editFood(args.getInt(ARG_FOOD_ID)) }
        deleteButton.setOnClickListener { showDeleteConfirmDialog() }

        viewModel.food.observe(viewLifecycleOwner, Observer {
            if (it == null) {
                (activity as FoodListActivity).showSnackBar("ストックを削除しました")
                dismiss()
            } else {
                this.setFood(it)
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        data ?: return

        when (requestCode) {
            REMOVE_BOX_REQUEST_CODE -> viewModel.removeFood()
        }
    }

    private fun setFood(food: Food) {
        nameView.text = food.name
        amountView.text = "${String.format("%.2f", food.amount)} ${food.unit.label}"
        increaseButton.text = food.unit.step.toString()
        decreaseButton.text = food.unit.step.toString()
    }

    private fun showNotices(foodId: Int) {
        val intent = Intent(activity, NoticeListActivity::class.java)

        intent.putExtra(getString(R.string.key_food_id), foodId)
        startActivity(intent)
        dismiss()
    }

    private fun editFood(foodId: Int) {
        val intent = Intent(activity, FoodActivity::class.java)

        intent.putExtra(getString(R.string.key_food_id), foodId)
        startActivity(intent)
        dismiss()
    }

    private fun showDeleteConfirmDialog() {
        val food = viewModel.food.value ?: return
        val fragment = ConfirmDialogFragment.newInstance(food.name, "削除していいですか？", food.id)

        fragment.setTargetFragment(null, REMOVE_BOX_REQUEST_CODE)
        fragment.show(childFragmentManager, "delete_box")
    }

    companion object {
        private const val ARG_FOOD_ID = "food_id"
        private const val REMOVE_BOX_REQUEST_CODE = 104

        fun newInstance(food: Food): FoodActionDialogFragment =
                FoodActionDialogFragment().apply {
                    arguments = Bundle().apply {
                        putInt(ARG_FOOD_ID, food.id)
                    }
                }
    }
}