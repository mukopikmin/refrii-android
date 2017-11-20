package com.refrii.client.views.activities

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.refrii.client.BasicCallback
import com.refrii.client.R
import com.refrii.client.RetrofitFactory
import com.refrii.client.models.Box
import com.refrii.client.models.Food
import com.refrii.client.views.fragments.CalendarPickerDialogFragment
import com.refrii.client.views.fragments.EditDoubleDialogFragment
import com.refrii.client.views.fragments.EditTextDialogFragment
import com.refrii.client.services.FoodService
import io.realm.Realm
import io.realm.RealmConfiguration
import kotterknife.bindView
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Response
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*

class FoodActivity : AppCompatActivity() {

    private val constraintLayout: ConstraintLayout by bindView(R.id.constraintLayout)
    private val progressBar: ProgressBar by bindView(R.id.foodProgressBar)
    private val foodEditedMessageTextView: TextView by bindView(R.id.foodEditedMessageTextView)
    private val editNameImageView: ImageView by bindView(R.id.editNameImageView)
    private val editAmountImageView: ImageView by bindView(R.id.editAmountImageView)
    private val editNoticeImageView: ImageView by bindView(R.id.editNoticeImageView)
    private val editExpirationDateImageView: ImageView by bindView(R.id.editExpirationDateImageView)
    private val toolbar: Toolbar by bindView(R.id.toolbar)
    private val foodNameTextView: TextView by bindView(R.id.foodNameTextView)
    private val amountTextView: TextView by bindView(R.id.amountTextView)
    private val noticeTextView: TextView by bindView(R.id.noticeTextView)
    private val expirationDateTextView: TextView by bindView(R.id.expirationDateTextView)
    private val createdUserTextView: TextView by bindView(R.id.createdUserTextView)
    private val updatedUserTextView: TextView by bindView(R.id.updatedUserTextView)
    private val foodBoxTextView: TextView by bindView(R.id.foodBoxTextView)
    private val fab: FloatingActionButton by bindView(R.id.fab)

    private var mFood: Food? = null
    private var mRealm: Realm? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food)

        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)

        constraintLayout.visibility = View.GONE
        foodEditedMessageTextView.visibility = View.GONE

        fab.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            updateFood(mFood!!)
        }

        Realm.setDefaultConfiguration(RealmConfiguration.Builder(this).build())
        mRealm = Realm.getDefaultInstance()

        fab.visibility = View.GONE

        editNameImageView.setOnClickListener {
            val fragment = EditTextDialogFragment.newInstance("Name", mFood!!.name!!)
            fragment.setTargetFragment(null, EDIT_NAME_REQUEST_CODE)
            fragment.show(fragmentManager, "edit_name")
        }

        editAmountImageView.setOnClickListener {
            val fragment = EditDoubleDialogFragment.newInstance("Amount", mFood!!.amount!!)
            fragment.setTargetFragment(null, EDIT_AMOUNT_REQUEST_CODE)
            fragment.show(fragmentManager, "edit_amount")
        }

        editNoticeImageView.setOnClickListener {
            val fragment = EditTextDialogFragment.newInstance("Notice", mFood!!.notice!!, true)
            fragment.setTargetFragment(null, EDIT_NOTICE_REQUEST_CODE)
            fragment.show(fragmentManager, "edit_notice")
        }

        editExpirationDateImageView.setOnClickListener {
            val fragment = CalendarPickerDialogFragment.newInstance(mFood!!.expirationDate!!)
            fragment.setTargetFragment(null, EDIT_EXPIRATION_DATE_REQUEST_CODE)
            fragment.show(fragmentManager, "edit_expiration_date")
        }
    }

    public override fun onStart() {
        super.onStart()

        val intent = intent
        val foodId = intent.getIntExtra("food_id", 0)
        val boxId = intent.getIntExtra("box_id", 0)

        setFood(foodId, boxId)
        syncFood(foodId)
    }

    public override fun onDestroy() {
        super.onDestroy()

        mRealm?.close()
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
        if (data == null || resultCode != Activity.RESULT_OK) return

        when (requestCode) {
            EDIT_NAME_REQUEST_CODE -> {
                mRealm!!.executeTransaction {
                    mFood!!.name = data.getStringExtra("text")
                    setFoodOnView(mFood!!)
                    onEdited()
                }
            }
            EDIT_AMOUNT_REQUEST_CODE -> {
                mRealm!!.executeTransaction {
                    mFood!!.amount = data.getDoubleExtra("number", 0.toDouble())
                    setFoodOnView(mFood!!)
                    onEdited()
                }
            }
            EDIT_NOTICE_REQUEST_CODE -> {
                mRealm!!.executeTransaction {
                    mFood!!.notice = data.getStringExtra("text")
                    setFoodOnView(mFood!!)
                    onEdited()
                }
            }
            EDIT_EXPIRATION_DATE_REQUEST_CODE -> {
                mRealm!!.executeTransaction {
                    val date = Date()
                    date.time = data.getLongExtra("date", 0)
                    mFood!!.expirationDate = date
                    setFoodOnView(mFood!!)
                    onEdited()
                }
            }
        }
    }

    private fun setFood(id: Int, boxId: Int) {
        val food = mRealm!!.where(Food::class.java)
                .equalTo("id", id)
                .findFirst()
        mFood = food
        val box = mRealm!!.where(Box::class.java)
                .equalTo("id", boxId)
                .findFirst()
        mRealm!!.executeTransaction { mFood!!.box = box }

        constraintLayout.visibility = View.VISIBLE
        progressBar.visibility = View.GONE

        setFoodOnView(food)
    }

    private fun setFoodOnView(food: Food) {
        val timeFormatter = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())
        val dateFormatter = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
        val daysLeft = (food.expirationDate!!.time - Date().time) / (24 * 60 * 60 * 1000)

        toolbar.title = food.name
        setSupportActionBar(toolbar)

        foodNameTextView.text = food.name
        foodBoxTextView.text = food.box!!.name
        amountTextView.text = "${food.amount} ${food.unit!!.label}"
        noticeTextView.text = food.notice
        createdUserTextView.text = timeFormatter.format(food.createdAt) + " by "// + food.createdUser!!.name
        updatedUserTextView.text = timeFormatter.format(food.updatedAt) + " by "// + food.updatedUser!!.name
        expirationDateTextView.text = dateFormatter.format(food.expirationDate)

        if (daysLeft < 0) {
            expirationDateTextView.append(" (" + Math.abs(daysLeft) + " days over)")
            expirationDateTextView.setTextColor(Color.RED)
        } else {
            expirationDateTextView.append(" (" + Math.abs(daysLeft) + " days left)")
        }
    }

    private fun updateFood(food: Food) {
        val service = RetrofitFactory.getClient(FoodService::class.java, this@FoodActivity)
        val formatter = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
        val body = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("name", food.name!!)
                .addFormDataPart("notice", food.notice!!)
                .addFormDataPart("amount", food.amount.toString())
                .addFormDataPart("box_id", food.box!!.id.toString())
                .addFormDataPart("unit_id", food.unit!!.id.toString())
                .addFormDataPart("expiration_date", formatter.format(food.expirationDate))
                .build()
        val call = service.updateFood(food.id, body)
        call.enqueue(object : BasicCallback<Food>(this@FoodActivity) {
            override fun onResponse(call: Call<Food>, response: Response<Food>) {
                if (response.code() == 200) {
                    val food = response.body()
                    val intent = Intent()

                    mRealm!!.executeTransaction { realm ->
                        realm.copyToRealmOrUpdate(food)
                    }

                    intent.putExtra("food_id", food!!.id)
                    setResult(Activity.RESULT_OK, intent)
                    progressBar.visibility = View.GONE
                    finish()
                } else {
                    Toast.makeText(this@FoodActivity, "Failed with status: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun syncFood(id: Int) {
        RetrofitFactory.getClient(FoodService::class.java, this).getFood(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object: Subscriber<Food>() {
                    override fun onError(e: Throwable?) {
                        e?.let { Toast.makeText(this@FoodActivity, it.message, Toast.LENGTH_LONG).show() }
                    }

                    override fun onCompleted() {
                        Log.d(TAG, "Update completed.")
                    }

                    override fun onNext(food: Food) {
                        mFood = food
                        setFoodOnView(food)

                        mRealm?.let { realm ->
                            realm.executeTransaction { realm.copyToRealmOrUpdate(food) }
                        }
                    }

                })
    }

    private fun onEdited() {
        foodEditedMessageTextView.visibility = View.VISIBLE
        fab.visibility = View.VISIBLE
    }

    private fun showProgressBar() {

    }

    companion object {
        private val TAG = "FoodActivity"
        private val EDIT_NAME_REQUEST_CODE = 100
        private val EDIT_AMOUNT_REQUEST_CODE = 101
        private val EDIT_NOTICE_REQUEST_CODE = 102
        private val EDIT_EXPIRATION_DATE_REQUEST_CODE = 103
    }
}
