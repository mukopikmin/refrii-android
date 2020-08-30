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
    lateinit var rootView: ConstraintLayout

    @BindView(R.id.foodProgressBar)
    lateinit var progressBar: ProgressBar

    @BindView(R.id.toolbar)
    lateinit var toolbar: Toolbar

    @BindView(R.id.nameEditText)
    lateinit var nameEditText: EditText

    @BindView(R.id.amountEditText)
    lateinit var amountEditText: EditText

    @BindView(R.id.expirationDateTextView)
    lateinit var expirationDateText: TextView

    @BindView(R.id.createdTextView)
    lateinit var createdAtText: TextView

    @BindView(R.id.updatedTextView)
    lateinit var updatedAtText: TextView

    @BindView(R.id.boxTextView)
    lateinit var boxNameText: TextView

    @BindView(R.id.fab)
    lateinit var fab: FloatingActionButton

    @BindView(R.id.unitsSpinner)
    lateinit var unitsSpinner: Spinner

    @BindView(R.id.shopPlanecyclerView)
    lateinit var shopPlanListRecycler: RecyclerView

    @BindView(R.id.addPlanButton)
    lateinit var addPlanButton: View

    @BindView(R.id.cameraImageView)
    lateinit var cameraImage: ImageView

    @Inject
    lateinit var presenter: FoodPresenter

    private lateinit var preference: SharedPreferences

    private var imageLoaded = false
    private var date: Date? = null
    private var image: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (application as App).getComponent().inject(this)

        setContentView(R.layout.activity_food)
        ButterKnife.bind(this)
        setSupportActionBar(toolbar)

        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeButtonEnabled(true)
        }

        preference = PreferenceManager.getDefaultSharedPreferences(applicationContext)

        initView()
    }

    private fun initView() {
        shopPlanListRecycler.layoutManager = LinearLayoutManager(this)
        fab.setOnClickListener { updateFood() }
        addPlanButton.setOnClickListener { showCreateShopPlanDialog() }
        expirationDateText.setOnClickListener { showEditDateDialog() }
        cameraImage.setOnClickListener { launchCameraOrShowImage() }
        cameraImage.setOnLongClickListener { showImageOptions() }
    }

    private fun updateFood() {
        val food = presenter.foodLiveData.value ?: return
        val unitLabel = unitsSpinner.selectedItem.toString()
        val unit = presenter.unitsLiveData.value
                ?.filter { it.user?.id == food.box.owner?.id }
                ?.single { it.label == unitLabel } ?: return
        var imageFile: File? = null

        food.name = nameEditText.text.toString()
        food.amount = amountEditText.text.toString().toDouble()
        food.unit = unit
        date?.let { food.expirationDate = it }

        if (image != null) {
            imageFile = File(filesDir, TEMP_IMAGE_FILENAME)
        }

        presenter.updateFood(food, imageFile)
    }

    private fun launchCameraOrShowImage() {
        val food = presenter.foodLiveData.value ?: return

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
        if (imageLoaded) {
            val image = (cameraImage.drawable as BitmapDrawable).bitmap
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

        presenter.init(this, foodId)
        presenter.getFood(foodId)
//        presenter.getUnits(boxId)
        presenter.getShopPlans(foodId)

        hideProgressBar()
    }

    override fun onStop() {
        super.onStop()

        presenter.terminate()
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
            EDIT_NAME_REQUEST_CODE -> nameEditText.setText(data.getStringExtra("text"))
            EDIT_AMOUNT_REQUEST_CODE -> amountEditText.setText(data.getDoubleExtra("number", 0.toDouble()).toString())
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

        image = bitmap
        onBeforeSetImage()
        cameraImage.setImageBitmap(bitmap)
    }

    private fun onBeforeSetImage() {
        val density = resources.displayMetrics.density
        val height = ((150 * density) + 0.5).toInt()
        val set = ConstraintSet()

        set.clone(rootView)
        set.constrainWidth(cameraImage.id, ConstraintSet.MATCH_CONSTRAINT)
        set.constrainHeight(cameraImage.id, height)
        set.applyTo(rootView)

        cameraImage.setPadding(0, 0, 0, 0)
        cameraImage.scaleType = ImageView.ScaleType.CENTER_CROP
        cameraImage.imageTintList = null
    }

    private fun updateExpirationDate(data: Intent?) {
        data ?: return

        val date = Date()

        this.date = date
        date.time = data.getLongExtra("date", 0)
        setExpirationDate(date)
    }

    private fun createShopPlan(data: Intent?) {
        val food = presenter.foodLiveData.value ?: return
        data ?: return

        val notice = null
        val amount = data.getDoubleExtra("key_amount", 0.toDouble())
        val date = Date(data.getLongExtra("key_date", Date().time))
        val shopPlan = ShopPlan.temp(notice, amount, date, food)

        presenter.createShopPlan(shopPlan)
    }

    override fun setFood(food: Food?) {
        val timeFormatter = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())

        toolbar.title = food?.name
        setSupportActionBar(toolbar)

        nameEditText.setText(food?.name)
        boxNameText.text = food?.box?.name
        amountEditText.setText(String.format("%.2f", food?.amount))
        createdAtText.text = "${timeFormatter.format(food?.createdAt)} (${food?.createdUser?.name})"
        updatedAtText.text = "${timeFormatter.format(food?.updatedAt)} (${food?.updatedUser?.name})"

        setExpirationDate(food?.expirationDate)

        presenter.unitsLiveData.value?.let { units ->
            val ids = units.map { it.id }
            val index = ids.indexOf(food?.unit?.id)

            unitsSpinner.setSelection(index)
        }

        food?.imageUrl?.let {
            imageLoaded = false

            Picasso.get()
                    .load(it)
                    .placeholder(R.drawable.ic_photo)
                    .into(cameraImage, object : Callback {
                        override fun onSuccess() {
                            onBeforeSetImage()
                            imageLoaded = true
                            image = (cameraImage.drawable as BitmapDrawable).bitmap
                        }

                        override fun onError(e: Exception?) {
                            showToast(e?.message)
                        }
                    })
        }
    }

    override fun setShopPlans(food: Food, shopPlans: List<ShopPlan>) {
        if (shopPlanListRecycler.adapter == null) {
            food.let {
                shopPlanListRecycler.adapter = ShopPlanRecyclerViewAdapter(shopPlans, it)
            }
        } else {
            val adapter = shopPlanListRecycler.adapter as ShopPlanRecyclerViewAdapter

            adapter.setOnClickListener(View.OnClickListener {
                val position = shopPlanListRecycler.getChildAdapterPosition(it)
                val shopPlan = adapter.getItemAtPosition(position)

                presenter.completeShopPlan(shopPlan)
            })
            adapter.setShopPlans(shopPlans)
        }
    }

    override fun onCompletedCompleteShopPlan(shopPlan: ShopPlan?) {
        shopPlan ?: return

        (shopPlanListRecycler.adapter as ShopPlanRecyclerViewAdapter).completeShopPlan(shopPlan)
    }

    private fun setExpirationDate(date: Date?) {
        val today = Date()
        val oneDayMilliSec = 24 * 60 * 60 * 1000
        val daysLeft = ((date?.time ?: today.time) - today.time) / oneDayMilliSec
        val dateFormatter = SimpleDateFormat(getString(R.string.format_date), Locale.getDefault())

        expirationDateText.text = dateFormatter.format(date)

        if (daysLeft < 0) {
            expirationDateText.append(" (${abs(daysLeft)} 日過ぎています)")
            expirationDateText.setTextColor(Color.RED)
        } else {
            expirationDateText.append(" (残り ${abs(daysLeft)} 日)")
            expirationDateText.setTextColor(Color.BLACK)
        }
    }

    fun setUnits(units: List<Unit>) {
        val labels = units.map { it.label }

        labels.let {
            unitsSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, it)
        }
    }

    override fun showEditDateDialog() {
        presenter.foodLiveData.value?.expirationDate?.let {
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
        progressBar.visibility = View.VISIBLE
        fab.hide()
    }

    override fun hideProgressBar() {
        progressBar.visibility = View.GONE
        fab.show()
    }

    override fun showSnackbar(message: String?) {
        message ?: return

        Snackbar.make(nameEditText, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun showToast(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun showCreateShopPlanDialog() {
        val label = presenter.foodLiveData.value?.unit?.label ?: return
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
