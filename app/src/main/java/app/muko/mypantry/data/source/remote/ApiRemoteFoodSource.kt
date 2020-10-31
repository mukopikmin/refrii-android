package app.muko.mypantry.data.source.remote

import app.muko.mypantry.data.models.Food
import app.muko.mypantry.data.source.data.ApiFoodDataSource
import app.muko.mypantry.data.source.remote.services.FoodService
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class ApiRemoteFoodSource(
        private val service: FoodService
) : ApiFoodDataSource {

    override fun getAll(): Flowable<List<Food>> {
        return service.getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getByBox(boxId: Int): Flowable<List<Food>> {
        return service.getByBox(boxId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    override fun get(id: Int): Flowable<Food?> {
        return service.get(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    override fun create(food: Food): Completable {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val body = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("name", food.name)
                .addFormDataPart("amount", food.amount.toString())
                .addFormDataPart("box_id", food.box.id.toString())
                .addFormDataPart("unit_id", food.unit.id.toString())
                .addFormDataPart("expiration_date", simpleDateFormat.format(food.expirationDate))
                .build()

        return service.create(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    override fun update(food: Food, imageFile: File?): Completable {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val params: HashMap<String, RequestBody> = HashMap()
        var image: MultipartBody.Part? = null

        imageFile?.let {
            image = MultipartBody.Part.createFormData(
                    "image",
                    "image.jpg",
                    RequestBody.create(MediaType.parse("image/jpeg"), it)
            )
        }

        food.name.let { params["name"] = RequestBody.create(MediaType.parse("multipart/form-data"), it) }
        food.amount.let { params["amount"] = RequestBody.create(MediaType.parse("multipart/form-data"), it.toString()) }
        food.expirationDate.let { params["expiration_date"] = RequestBody.create(MediaType.parse("multipart/form-data"), simpleDateFormat.format(it)) }
        food.box.id.let { params["box_id"] = RequestBody.create(MediaType.parse("multipart/form-data"), it.toString()) }
        food.unit.id.let { params["unit_id"] = RequestBody.create(MediaType.parse("multipart/form-data"), it.toString()) }

        return service.update(food.id, params, image)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }


    override fun remove(food: Food): Completable {
        return service.remove(food.id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    override fun createNotice(food: Food, text: String): Flowable<Food> {
        val bodyBuilder = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("text", text)

        return service.create(food.id, bodyBuilder.build())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }


}