package com.refrii.client

import android.app.Activity
import android.app.Dialog
import android.app.DialogFragment
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Response

class FoodActivity : AppCompatActivity() {

    private var food: Food? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food)

        val toolbar = findViewById<Toolbar>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)

        val constraintLayout = findViewById<ConstraintLayout>(R.id.constraintLayout) as ConstraintLayout
        val progressBar = findViewById<ProgressBar>(R.id.foodProgressBar) as ProgressBar
        val foodEditedMessageTextView = findViewById<TextView>(R.id.foodEditedMessageTextView) as TextView
        val editNameImageView = findViewById<TextView>(R.id.editNameImageView) as ImageView
        val editAmountImageView = findViewById<ImageView>(R.id.editAmountImageView) as ImageView
        val editNoticeImageView = findViewById<ImageView>(R.id.editNoticeImageView) as ImageView
        val editExpirationDateImageView = findViewById<ImageView>(R.id.editExpirationDateImageView) as ImageView

        constraintLayout.visibility = View.GONE
        foodEditedMessageTextView.visibility = View.GONE

        val fab = findViewById<FloatingActionButton>(R.id.fab) as FloatingActionButton
        fab.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            val service = RetrofitFactory.getClient(FoodService::class.java, this@FoodActivity)
            val formatter = SimpleDateFormat("yyyy/MM/dd")
            val body = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("name", food!!.name!!)
                    .addFormDataPart("notice", food!!.notice!!)
                    .addFormDataPart("amount", food!!.amount.toString())
                    .addFormDataPart("box_id", food!!.box!!.id.toString())
                    .addFormDataPart("unit_id", food!!.unit!!.id.toString())
                    .addFormDataPart("expiration_date", formatter.format(food!!.expirationDate))
                    .build()
            val call = service.updateFood(food!!.id, body)
            call.enqueue(object : BasicCallback<Food>(this@FoodActivity) {
                override fun onResponse(call: Call<Food>, response: Response<Food>) {
                    if (response.code() == 200) {
                        val food = response.body()
                        val intent = Intent()
                        intent.putExtra("food", food)
                        setResult(Activity.RESULT_OK, intent)
                        progressBar.visibility = View.GONE
                        finish()
                    } else {
                        Log.d(TAG, "Failed with status: " + response.code())
                    }
                }
            })
        }

        fab.visibility = View.GONE

        editNameImageView.setOnClickListener {
            val args = Bundle()
            args.putSerializable("food", food)
            val fragment = EditNameDialogFragment()
            fragment.setFood(food)
            fragment.show(fragmentManager, "edit_name")
        }

        editAmountImageView.setOnClickListener {
            val fragment = EditAmountDialogFragment()
            fragment.setFood(food)
            fragment.show(fragmentManager, "edit_amount")
        }

        editNoticeImageView.setOnClickListener {
            val fragment = EditNoticeDialogFragment()
            fragment.setFood(food)
            fragment.show(fragmentManager, "edit_notice")
        }

        editExpirationDateImageView.setOnClickListener {
            val fragment = EditDateDialogFragment()
            fragment.setFood(food)
            fragment.show(fragmentManager, "edit_expiration_date")
        }
    }

    public override fun onResume() {
        super.onResume()

        val intent = intent
        val foodId = intent.getIntExtra("foodId", 0)
        setFood(foodId)
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

    private fun setFood(id: Int) {
        val constraintLayout = findViewById<ConstraintLayout>(R.id.constraintLayout) as ConstraintLayout
        val progressBar = findViewById<ProgressBar>(R.id.foodProgressBar) as ProgressBar
        val service = RetrofitFactory.getClient(FoodService::class.java, this@FoodActivity)
        val call = service.getFood(id)
        call.enqueue(object : BasicCallback<Food>(this@FoodActivity) {
            override fun onResponse(call: Call<Food>, response: Response<Food>) {
                super.onResponse(call, response)

                if (response.code() == 200) {
                    food = response.body()
                    setFoodOnView(food)
                    constraintLayout.visibility = View.VISIBLE
                    progressBar.visibility = View.GONE
                }
            }

            override fun onFailure(call: Call<Food>, t: Throwable) {
                Log.d(TAG, t.message)
                Toast.makeText(this@FoodActivity, t.message, Toast.LENGTH_LONG).show()
            }
        })
    }

    fun setFoodOnView(food: Food?) {
        val toolbar = findViewById<Toolbar>(R.id.toolbar) as Toolbar
        val foodNameTextView = findViewById<TextView>(R.id.foodNameTextView) as TextView
        val amountTextView = findViewById<TextView>(R.id.amountTextView) as TextView
        val noticeTextView = findViewById<TextView>(R.id.noticeTextView) as TextView
        val expirationDateTextView = findViewById<TextView>(R.id.expirationDateTextView) as TextView
        val createdUserTextView = findViewById<TextView>(R.id.createdUserTextView) as TextView
        val updatedUserTextView = findViewById<TextView>(R.id.updatedUserTextView) as TextView
        val foodBoxTextView = findViewById<TextView>(R.id.foodBoxTextView) as TextView

        val timeFormatter = SimpleDateFormat("yyyy/MM/dd HH:mm")
        val dateFormatter = SimpleDateFormat("yyyy/MM/dd")

        val daysLeft = (food!!.expirationDate!!.time - Date().time) / (24 * 60 * 60 * 1000)

        toolbar.title = food.name
        foodNameTextView.text = food.name
        foodBoxTextView.text = food.box!!.name
        amountTextView.text = food.amount.toString() + " " + food.unit!!.label
        noticeTextView.text = food.notice
        expirationDateTextView.text = dateFormatter.format(food.expirationDate)
        if (daysLeft < 0) {
            expirationDateTextView.append(" (" + Math.abs(daysLeft) + " days over)")
            expirationDateTextView.setTextColor(Color.RED)
        } else {
            expirationDateTextView.append(" (" + Math.abs(daysLeft) + " days left)")
        }
        createdUserTextView.text = timeFormatter.format(food.createdAt) + " by " + food.createdUser!!.name
        updatedUserTextView.text = timeFormatter.format(food.updatedAt) + " by " + food.updatedUser!!.name
    }

    fun onEdited() {
        val foodEditedMessageTextView = findViewById<TextView>(R.id.foodEditedMessageTextView) as TextView
        val fab = findViewById<FloatingActionButton>(R.id.fab) as FloatingActionButton

        foodEditedMessageTextView.visibility = View.VISIBLE
        fab.visibility = View.VISIBLE
    }

    class EditNameDialogFragment : DialogFragment() {
        private var mFood: Food? = null
        private var mEditText: EditText? = null
        private val onPositiveButtonClickListener = DialogInterface.OnClickListener { dialogInterface, i ->
            mFood!!.name = mEditText!!.text.toString()
            (activity as FoodActivity).setFoodOnView(mFood)
            (activity as FoodActivity).onEdited()
        }

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val inflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val content = inflater.inflate(R.layout.edit_text_dialog, null)
            mEditText = content.findViewById(R.id.editText)
            mEditText!!.setText(mFood!!.name)

            val builder = AlertDialog.Builder(activity)
                    .setTitle("Name")
                    .setView(content)
                    .setPositiveButton("OK", onPositiveButtonClickListener)
                    .setNegativeButton("Cancel") { dialogInterface, i -> }

            return builder.create()
        }

        fun setFood(food: Food?) {
            mFood = food
        }
    }

    class EditAmountDialogFragment : DialogFragment() {
        private var mFood: Food? = null
        private var mEditText: EditText? = null

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val inflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val content = inflater.inflate(R.layout.edit_text_dialog, null)
            mEditText = content.findViewById(R.id.editText)
            mEditText!!.inputType = InputType.TYPE_CLASS_NUMBER
            mEditText!!.setText(mFood!!.amount.toString())

            val builder = AlertDialog.Builder(activity)
                    .setTitle("Amount")
                    .setView(content)
                    .setPositiveButton("OK") { dialogInterface, i ->
                        mFood!!.amount = java.lang.Double.valueOf(mEditText!!.text.toString())
                        (activity as FoodActivity).setFoodOnView(mFood)
                        (activity as FoodActivity).onEdited()
                    }
                    .setNegativeButton("Cancel") { dialogInterface, i -> }

            return builder.create()
        }

        fun setFood(food: Food?) {
            mFood = food
        }
    }

    class EditNoticeDialogFragment : DialogFragment() {
        private var mFood: Food? = null
        private var mEditText: EditText? = null

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val inflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val content = inflater.inflate(R.layout.edit_text_dialog, null)
            mEditText = content.findViewById(R.id.editText)
            mEditText!!.setSingleLine(false)
            mEditText!!.maxLines = 5
            mEditText!!.setText(mFood!!.notice)

            val builder = AlertDialog.Builder(activity)
                    .setTitle("Notice")
                    .setView(content)
                    .setPositiveButton("OK") { dialogInterface, i ->
                        mFood!!.notice = mEditText!!.text.toString()
                        (activity as FoodActivity).setFoodOnView(mFood)
                        (activity as FoodActivity).onEdited()
                    }
                    .setNegativeButton("Cancel") { dialogInterface, i -> }

            return builder.create()
        }

        fun setFood(food: Food?) {
            mFood = food
        }
    }

    class EditDateDialogFragment : DialogFragment() {
        private var mFood: Food? = null
        private var mDatePicker: DatePicker? = null

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val inflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val content = inflater.inflate(R.layout.edit_date_dialog, null)
            mDatePicker = content.findViewById(R.id.datePicker)

            val calendar = Calendar.getInstance()
            calendar.time = mFood!!.expirationDate
            mDatePicker!!.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))

            val builder = AlertDialog.Builder(activity)
                    .setView(content)
                    .setPositiveButton("OK") { dialogInterface, i ->
                        val calendar = Calendar.getInstance()
                        calendar.set(mDatePicker!!.year, mDatePicker!!.month, mDatePicker!!.dayOfMonth)
                        mFood!!.expirationDate = calendar.time
                        (activity as FoodActivity).setFoodOnView(mFood)
                        (activity as FoodActivity).onEdited()
                    }
                    .setNegativeButton("Cancel") { dialogInterface, i -> }

            return builder.create()
        }

        fun setFood(food: Food?) {
            mFood = food
        }
    }

    companion object {
        private val TAG = "FoodActivity"
    }
}
