package app.muko.mypantry.food

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.MediaStore
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.muko.mypantry.App
import app.muko.mypantry.BuildConfig
import app.muko.mypantry.R
import app.muko.mypantry.data.models.Food
import app.muko.mypantry.data.models.ShopPlan
import app.muko.mypantry.data.models.Unit
import app.muko.mypantry.dialogs.CalendarPickerDialogFragment
import app.muko.mypantry.dialogs.CreateShopPlanDialogFragment
import app.muko.mypantry.dialogs.ImageViewDialogFragment
import app.muko.mypantry.dialogs.OptionsPickerDialogFragment
import butterknife.BindView
import butterknife.ButterKnife
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.math.abs

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

    @Inject
    lateinit var mPresenter: FoodPresenter

    private lateinit var mPreference: SharedPreferences

    private var mImageLoaded = false
    private var mDate: Date? = null
    private var mImage: Bitmap? = null

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

        mPreference = PreferenceManager.getDefaultSharedPreferences(applicationContext)

        initView()
    }

    private fun initView() {
        mPresenter.takeView(this)

        mRecyclerView.layoutManager = LinearLayoutManager(this)
        mFab.setOnClickListener { mPresenter.getUnits(1) }
        mAddPlanButton.setOnClickListener { showCreateShopPlanDialog() }
        mExpirationDate.setOnClickListener { showEditDateDialog() }
        mCameraImageView.setOnClickListener { launchCameraOrShowImage() }
        mCameraImageView.setOnLongClickListener { showImageOptions() }
    }

    private fun updateFood() {
        val food = mPresenter.foodLiveData.value ?: return
//        val name = mName.text.toString()
//        val amount = mAmount.text.toString().toDouble()
        val unitLabel = mUnitsSpinner.selectedItem.toString()
        val unit = mPresenter.unitsLiveData.value?.single { it.label == unitLabel } ?: return

        food.name = mName.text.toString()
        food.amount = mAmount.text.toString().toDouble()
        food.unit = unit

        mPresenter.updateFood(food)
    }

    private fun launchCameraOrShowImage() {
        val food = mPresenter.foodLiveData.value ?: return

        if (food.imageUrl.isNullOrEmpty()) {
            launchCamera()
        } else {
            showImageDialog()
        }
    }

    private fun showImageOptions(): Boolean {
        val options = arrayOf(
                getString(R.string.message_take_picture),
                getString(R.string.message_show_image),
                getString(R.string.message_cancel)
        )
        val fragment = OptionsPickerDialogFragment.newInstance(null, options, null)

        fragment.setTargetFragment(null, IMAGE_OPTIONS_REQUEST_CODE)
        fragment.show(supportFragmentManager, "image_option")

        return true
    }

    private fun showImageDialog() {
        if (mImageLoaded) {
            val image = (mCameraImageView.drawable as BitmapDrawable).bitmap
            val fragment = ImageViewDialogFragment.newInstance(image)

            fragment.setTargetFragment(null, EDIT_EXPIRATION_DATE_REQUEST_CODE)
            fragment.show(supportFragmentManager, "edit_expiration_date")
        } else {
            showToast(getString(R.string.message_loading_image))
        }
    }

    private fun launchCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val file = createOutputFile()
        val uri = getTempImageUri(file)

        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        startActivityForResult(intent, RESULT_CAMERA)
    }

    private fun getTempImageUri(file: File): Uri {
        return FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".fileprovider", file)
    }

    private fun createOutputFile(): File {
        val tempFile = File(filesDir, TEMP_IMAGE_FILENAME)

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

        val intent = intent
        val foodId = intent.getIntExtra(getString(R.string.key_food_id), 0)
        val boxId = intent.getIntExtra(getString(R.string.key_box_id), 0)

        mPresenter.initLiveData(foodId)
        mPresenter.getFood(foodId)
        mPresenter.getUnits(boxId)
        mPresenter.getShopPlans(foodId)

        mPresenter.foodLiveData.observe(this, Observer {
            setFood(it)

            val shopPlans = mPresenter.shopPlansLiveData.value ?: return@Observer
            setShopPlans(it, shopPlans)
        })
        mPresenter.shopPlansLiveData.observe(this, Observer {
            val food = mPresenter.foodLiveData.value ?: return@Observer

            setShopPlans(food, it)
        })
        mPresenter.unitsLiveData.observe(this, Observer {
            setUnits(it)
        })

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

        if (resultCode != Activity.RESULT_OK) return

        when (requestCode) {
            RESULT_CAMERA -> onTookPicture()
        }

        if (data == null) return

        when (requestCode) {
            EDIT_NAME_REQUEST_CODE -> mName.setText(data.getStringExtra("text")) //mPresenter.updateName(data.getStringExtra("text"))
            EDIT_AMOUNT_REQUEST_CODE -> mAmount.setText(data.getDoubleExtra("number", 0.toDouble()).toString()) //mPresenter.updateAmount(data.getDoubleExtra("number", 0.toDouble()))
            EDIT_EXPIRATION_DATE_REQUEST_CODE -> updateExpirationDate(data)
            CREATE_SHOP_PLAN_REQUEST_CODE -> createShopPlan(data)
            IMAGE_OPTIONS_REQUEST_CODE -> {
                when (data.getIntExtra("option", -1)) {
                    // Take a picture
                    0 -> launchCamera()
                    // Show the picture
                    1 -> showImageDialog()
                    // Cancel
                    else -> return
                }
            }
        }
    }

    private fun onTookPicture() {
        val file = File(filesDir, TEMP_IMAGE_FILENAME)
        val uri = getTempImageUri(file)
        val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)

        mImage = bitmap
        onBeforeSetImage()
        mCameraImageView.setImageBitmap(bitmap)
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

        mDate = date
        date.time = data.getLongExtra("date", 0)
        setExpirationDate(date)
    }

    private fun createShopPlan(data: Intent?) {
        val food = mPresenter.foodLiveData.value ?: return
        data ?: return

        val notice = null
        val amount = data.getDoubleExtra("key_amount", 0.toDouble())
        val date = Date(data.getLongExtra("key_date", Date().time))
        val shopPlan = ShopPlan.temp(notice, amount, date, food)

        mPresenter.createShopPlan(shopPlan)
    }

    private fun setFood(food: Food?) {
        val timeFormatter = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())

        mToolbar.title = food?.name
        setSupportActionBar(mToolbar)

        mName.setText(food?.name)
        mBoxName.text = food?.box?.name
        mAmount.setText(String.format("%.2f", food?.amount))
        mCreated.text = "${timeFormatter.format(food?.createdAt)} (${food?.createdUser?.name})"
        mUpdate.text = "${timeFormatter.format(food?.updatedAt)} (${food?.updatedUser?.name})"

        setExpirationDate(food?.expirationDate)

        mPresenter.unitsLiveData.value?.let { units ->
            val ids = units.map { it.id }
            val index = ids.indexOf(food?.unit?.id)

            mUnitsSpinner.setSelection(index)
        }

        food?.imageUrl?.let {
            mImageLoaded = false

            Picasso.get()
                    .load(it)
                    .placeholder(R.drawable.ic_photo)
                    .into(mCameraImageView, object : Callback {
                        override fun onSuccess() {
                            onBeforeSetImage()
                            mImageLoaded = true
                            mImage = (mCameraImageView.drawable as BitmapDrawable).bitmap
                        }

                        override fun onError(e: Exception?) {
                            showToast(e?.message)
                        }
                    })
        }
    }

    private fun setShopPlans(food: Food, shopPlans: List<ShopPlan>) {
        if (mRecyclerView.adapter == null) {
            food.let {
                mRecyclerView.adapter = ShopPlanRecyclerViewAdapter(shopPlans, it)
            }
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

    private fun setExpirationDate(date: Date?) {
        val today = Date()
        val oneDayMilliSec = 24 * 60 * 60 * 1000
        val daysLeft = ((date?.time ?: today.time) - today.time) / oneDayMilliSec
        val dateFormatter = SimpleDateFormat(getString(R.string.format_date), Locale.getDefault())

        mExpirationDate.text = dateFormatter.format(date)

        if (daysLeft < 0) {
            mExpirationDate.append(" (${abs(daysLeft)} 日過ぎています)")
            mExpirationDate.setTextColor(Color.RED)
        } else {
            mExpirationDate.append(" (残り ${abs(daysLeft)} 日)")
            mExpirationDate.setTextColor(Color.BLACK)
        }
    }

    fun setUnits(units: List<Unit>) {
        val labels = units.map { it.label }?.toMutableList()

        labels.let {
            mUnitsSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, it)
        }
    }

    override fun showEditDateDialog() {
        mPresenter.foodLiveData.value?.expirationDate?.let {
            val fragment = CalendarPickerDialogFragment.newInstance(it)

            fragment.setTargetFragment(null, EDIT_EXPIRATION_DATE_REQUEST_CODE)
            fragment.show(supportFragmentManager, "edit_expiration_date")
        }
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

    override fun showCreateShopPlanDialog() {
        val label = mPresenter.foodLiveData.value?.unit?.label ?: return
        val fragment = CreateShopPlanDialogFragment.newInstance(label)

        fragment.setTargetFragment(null, CREATE_SHOP_PLAN_REQUEST_CODE)
        fragment.show(supportFragmentManager, "create_shop_plan")
    }

    companion object {
        private const val EDIT_NAME_REQUEST_CODE = 100
        private const val EDIT_AMOUNT_REQUEST_CODE = 101
        private const val EDIT_EXPIRATION_DATE_REQUEST_CODE = 103
        private const val CREATE_SHOP_PLAN_REQUEST_CODE = 104
        private const val RESULT_CAMERA = 105
        private const val IMAGE_OPTIONS_REQUEST_CODE = 106
        private const val TEMP_IMAGE_FILENAME = "temp/temp.jpg"
    }
}
