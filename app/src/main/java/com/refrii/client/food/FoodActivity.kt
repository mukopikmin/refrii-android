package com.refrii.client.food

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.refrii.client.App
import com.refrii.client.BuildConfig
import com.refrii.client.R
import com.refrii.client.data.models.Food
import com.refrii.client.data.models.ShopPlan
import com.refrii.client.data.models.Unit
import com.refrii.client.dialogs.CalendarPickerDialogFragment
import com.refrii.client.dialogs.CreateShopPlanDialogFragment
import com.refrii.client.dialogs.ImageViewDialogFragment
import com.refrii.client.dialogs.OptionsPickerDialogFragment
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class FoodActivity : AppCompatActivity(), FoodContract.View {

    @BindView(R.id.constraintLayout)
    lateinit var mConstraintLayout: ConstraintLayout
    @BindView(R.id.foodProgressBar)
    lateinit var mProgressBar: ProgressBar
    @BindView(R.id.toolbar)
    lateinit var mToolbar: Toolbar
    @BindView(R.id.nameEditText)
    lateinit var mName: EditText
    @BindView(R.id.amountEditText)
    lateinit var mAmount: EditText
    @BindView(R.id.expirationDateTextView)
    lateinit var mExpirationDate: TextView
    @BindView(R.id.createdTextView)
    lateinit var mCreated: TextView
    @BindView(R.id.updatedTextView)
    lateinit var mUpdate: TextView
    @BindView(R.id.boxTextView)
    lateinit var mBoxName: TextView
    @BindView(R.id.fab)
    lateinit var mFab: FloatingActionButton
    @BindView(R.id.unitsSpinner)
    lateinit var mUnitsSpinner: Spinner
    @BindView(R.id.shopPlanecyclerView)
    lateinit var mRecyclerView: RecyclerView
    @BindView(R.id.addPlanButton)
    lateinit var mAddPlanButton: View
    @BindView(R.id.cameraImageView)
    lateinit var mCameraImageView: ImageView

    private var mUnitIds: List<Int>? = null
    private var mUnitLabels: MutableList<String?>? = null
    private var mImageUri: Uri? = null
    private val mOnUnitSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {}

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            val spinnerParent = parent as Spinner
            val item = spinnerParent.selectedItem as String

            mUnitIds?.let { ids ->
                mUnitLabels?.indexOf(item)?.let {
                    mPresenter.selectUnit(ids[it])
                }
            }
        }
    }

    @Inject
    lateinit var mPresenter: FoodPresenter

    private lateinit var mPreference: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (application as App).getComponent().inject(this)

        setContentView(R.layout.activity_food)
        ButterKnife.bind(this)
        setSupportActionBar(mToolbar)

        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeButtonEnabled(true)
        }

        val intent = intent
        val foodId = intent.getIntExtra(getString(R.string.key_food_id), 0)
        val boxId = intent.getIntExtra(getString(R.string.key_box_id), 0)

        mPresenter.takeView(this)
        mPresenter.getFood(foodId)
        mPresenter.getUnits(boxId)

        mPreference = PreferenceManager.getDefaultSharedPreferences(applicationContext)

        mRecyclerView.layoutManager = LinearLayoutManager(this)

        mFab.setOnClickListener { mPresenter.updateFood() }
        mAddPlanButton.setOnClickListener { mPresenter.showCreateShopPlanDialog() }
        mName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                mPresenter.updateName(s.toString())
            }
        })
        mAmount.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                mPresenter.updateAmount(s.toString().toDouble())
            }
        })
        mUnitsSpinner.onItemSelectedListener = mOnUnitSelectedListener
        mExpirationDate.setOnClickListener { mPresenter.editExpirationDate() }
        mCameraImageView.setOnClickListener { mPresenter.showImage() }
        mCameraImageView.setOnLongClickListener { showImageOptions() }
    }

    private fun showImageOptions(): Boolean {
        val options = arrayOf(
                "別の画像を撮影する",
                "画像を表示する",
                "キャンセル"
        )
        val fragment = OptionsPickerDialogFragment.newInstance(null, options, null)

        fragment.setTargetFragment(null, IMAGE_OPTIONS_REQUEST_CODE)
        fragment.show(supportFragmentManager, "image_option")

        return true
    }

    override fun showImageDialog(imageUrl: String) {
        val image = (mCameraImageView.drawable as BitmapDrawable).bitmap
        val fragment = ImageViewDialogFragment.newInstance(image)

        fragment.setTargetFragment(null, EDIT_EXPIRATION_DATE_REQUEST_CODE)
        fragment.show(supportFragmentManager, "edit_expiration_date")
    }

    private fun launchCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val file = createOutputFile()

        mImageUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".fileprovider", file)

        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri)
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        startActivityForResult(intent, RESULT_CAMERA)
    }

    private fun createOutputFile(): File {
        val formatter = SimpleDateFormat("yyyyMMddhhmmss", Locale.getDefault())
        val filename = formatter.format(Date())
        val tempFile = File(filesDir, "temp/$filename")

        if (!tempFile.exists()) {
            try {
                tempFile.parentFile.mkdirs()
                tempFile.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        return tempFile
    }


    override fun onStart() {
        super.onStart()

        hideProgressBar()
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

        when (requestCode) {
            RESULT_CAMERA -> onTookPicture()
        }

        if (data == null || resultCode != Activity.RESULT_OK) return

        when (requestCode) {
            EDIT_NAME_REQUEST_CODE -> mPresenter.updateName(data.getStringExtra("text"))
            EDIT_AMOUNT_REQUEST_CODE -> mPresenter.updateAmount(data.getDoubleExtra("number", 0.toDouble()))
            EDIT_NOTICE_REQUEST_CODE -> mPresenter.updateNotice(data.getStringExtra("text"))
            EDIT_EXPIRATION_DATE_REQUEST_CODE -> updateExpirationDate(data)
            CREATE_SHOP_PLAN_REQUEST_CODE -> createShopPlan(data)
            IMAGE_OPTIONS_REQUEST_CODE -> {
                when (data.getIntExtra("option", -1)) {
                    // Take a picture
                    0 -> launchCamera()
                    // Show the picture
                    1 -> mPresenter.showImage()
                    // Cancel
                    else -> return
                }
            }
        }
    }

    private fun onTookPicture() {
        val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, mImageUri)

        onBeforeSetImage()
        mCameraImageView.setImageBitmap(bitmap)
        mPresenter.updateImage(bitmap)
    }

    private fun onBeforeSetImage() {
        val density = resources.displayMetrics.density
        val height = ((150 * density) + 0.5).toInt()
        val set = ConstraintSet()

        set.clone(mConstraintLayout)
        set.constrainWidth(mCameraImageView.id, ConstraintSet.MATCH_CONSTRAINT)
        set.constrainHeight(mCameraImageView.id, height)
        set.applyTo(mConstraintLayout)

        mCameraImageView.setPadding(0, 0, 0, 0)
        mCameraImageView.scaleType = ImageView.ScaleType.CENTER_CROP
        mCameraImageView.imageTintList = null
    }

    private fun updateExpirationDate(data: Intent?) {
        data ?: return

        val date = Date()

        date.time = data.getLongExtra("date", 0)
        mPresenter.updateExpirationDate(date)
    }

    private fun createShopPlan(data: Intent?) {
        data ?: return

        val amount = data.getDoubleExtra("key_amount", 0.toDouble())
        val date = Date(data.getLongExtra("key_date", Date().time))

        mPresenter.createShopPlan(amount, date)
    }

    override fun setFood(food: Food?) {
        val timeFormatter = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())

        mToolbar.title = food?.name
        setSupportActionBar(mToolbar)

        mName.setText(food?.name)
        mBoxName.text = food?.box?.name
        mAmount.setText(String.format("%.2f", food?.amount))
        mCreated.text = "${timeFormatter.format(food?.createdAt)} (${food?.createdUser?.name})"
        mUpdate.text = "${timeFormatter.format(food?.updatedAt)} (${food?.updatedUser?.name})"

        setExpirationDate(food?.expirationDate)

        food?.imageUrl?.let {
            Picasso.get()
                    .load(it)
                    .placeholder(R.drawable.ic_photo)
                    .into(mCameraImageView, object : Callback {
                        override fun onSuccess() {
                            onBeforeSetImage()
                        }

                        override fun onError(e: Exception?) {
                            showToast(e?.message)
                        }
                    })
        }
    }

    override fun setShopPlans(food: Food?, shopPlans: List<ShopPlan>?) {
        food ?: return
        shopPlans ?: return

        if (mRecyclerView.adapter == null) {
            mRecyclerView.adapter = ShopPlanRecyclerViewAdapter(shopPlans, food)
        } else {
            val adapter = mRecyclerView.adapter as ShopPlanRecyclerViewAdapter

            adapter.setOnClickListener(View.OnClickListener {
                val position = mRecyclerView.getChildAdapterPosition(it)
                val shopPlan = adapter.getItemAtPosition(position)

                mPresenter.completeShopPlan(shopPlan)
            })
            adapter.setShopPlans(shopPlans)
        }
    }

    override fun onCompletedCompleteShopPlan(shopPlan: ShopPlan?) {
        shopPlan ?: return

        (mRecyclerView.adapter as ShopPlanRecyclerViewAdapter).completeShopPlan(shopPlan)
    }

    override fun setExpirationDate(date: Date?) {
        val today = Date()
        val oneDayMilliSec = 24 * 60 * 60 * 1000
        val daysLeft = ((date?.time ?: today.time) - today.time) / oneDayMilliSec
        val dateFormatter = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())

        mExpirationDate.text = dateFormatter.format(date)

        if (daysLeft < 0) {
            mExpirationDate.append(" (${Math.abs(daysLeft)} 日過ぎています)")
            mExpirationDate.setTextColor(Color.RED)
        } else {
            mExpirationDate.append(" (残り ${Math.abs(daysLeft)} 日)")
            mExpirationDate.setTextColor(Color.BLACK)
        }
    }

    override fun setUnits(units: List<Unit>?) {
        units ?: return

        mUnitLabels = units.map { it.label }.toMutableList()
        mUnitIds = units.map { it.id }

        mUnitLabels?.let {
            mUnitsSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, it)
        }
    }

    override fun setSelectedUnit(id: Int?) {
        val index = mUnitIds?.indexOf(id) ?: 0

        mUnitsSpinner.setSelection(index)
    }

    override fun showEditDateDialog(date: Date?) {
        date ?: return

        val fragment = CalendarPickerDialogFragment.newInstance(date)

        fragment.setTargetFragment(null, EDIT_EXPIRATION_DATE_REQUEST_CODE)
        fragment.show(supportFragmentManager, "edit_expiration_date")
    }

    override fun onUpdateCompleted(food: Food?) {
        val intent = Intent()

        intent.putExtra(getString(R.string.key_food_name), food?.name)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    override fun showProgressBar() {
        mProgressBar.visibility = View.VISIBLE
        mFab.hide()
    }

    override fun hideProgressBar() {
        mProgressBar.visibility = View.GONE
        mFab.show()
    }

    override fun showSnackbar(message: String?) {
        message ?: return

        Snackbar.make(mName, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun showToast(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun showCreateShopPlanDialog(food: Food?) {
        val label = food?.unit?.label ?: return
        val fragment = CreateShopPlanDialogFragment.newInstance(label)

        fragment.setTargetFragment(null, CREATE_SHOP_PLAN_REQUEST_CODE)
        fragment.show(supportFragmentManager, "create_shop_plan")
    }

    companion object {
        private const val EDIT_NAME_REQUEST_CODE = 100
        private const val EDIT_AMOUNT_REQUEST_CODE = 101
        private const val EDIT_NOTICE_REQUEST_CODE = 102
        private const val EDIT_EXPIRATION_DATE_REQUEST_CODE = 103
        private const val CREATE_SHOP_PLAN_REQUEST_CODE = 104
        private const val RESULT_CAMERA = 105
        private const val IMAGE_OPTIONS_REQUEST_CODE = 106
    }
}
