package app.muko.mypantry.fragments.message

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import app.muko.mypantry.R
import app.muko.mypantry.newfood.NewFoodActivity
import butterknife.BindView
import butterknife.ButterKnife

class EmptyBoxMessageFragment : Fragment() {

    @BindView(R.id.addButton)
    lateinit var addButton: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_empty_box_message, container, false)

        ButterKnife.bind(this, view)

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val args = arguments
        val boxId = args?.getInt("box_id") ?: return

        addButton.setOnClickListener { addFood(boxId) }
    }

    private fun addFood(boxId: Int) {
        val intent = Intent(activity, NewFoodActivity::class.java)

        intent.putExtra(getString(R.string.key_box_id), boxId)
        startActivityForResult(intent, ADD_FOOD_REQUEST_CODE)
    }

    companion object {
        private const val ADD_BOX_REQUEST_CODE = 101
        private const val ADD_FOOD_REQUEST_CODE = 102
        private const val EDIT_FOOD_REQUEST_CODE = 103
        private const val REMOVE_FOOD_REQUEST_CODE = 104
        private const val REMOVE_BOX_REQUEST_CODE = 105
        private const val CREATE_BOX_REQUEST_CODE = 106
        private const val SHOW_NOTICE_REQUEST_CODE = 107

        @JvmStatic
        fun newInstance(boxId: Int): EmptyBoxMessageFragment {
            val fragment = EmptyBoxMessageFragment()
            val args = Bundle()

            args.putInt("box_id", boxId)
            fragment.arguments = args

            return fragment
        }
    }
}