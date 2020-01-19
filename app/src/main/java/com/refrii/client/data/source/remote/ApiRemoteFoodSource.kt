package com.refrii.client.data.source.remote

import android.content.Context
import android.graphics.Bitmap
import com.refrii.client.data.models.Box
import com.refrii.client.data.models.Food
import com.refrii.client.data.models.ShopPlan
import com.refrii.client.data.models.Unit
import com.refrii.client.data.source.remote.services.FoodService
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Retrofit
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*


class ApiRemoteFoodSource(
        private val mContext: Context,
        private val mRetrofit: Retrofit
) {

    fun getFoods(): Observable<List<Food>> {
        return mRetrofit.create(FoodService::class.java)
                .getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun getFood(id: Int): Observable<Food> {
        return mRetrofit.create(FoodService::class.java)
                .getById(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun getShopPlansForFood(id: Int): Observable<List<ShopPlan>> {
        return mRetrofit.create(FoodService::class.java)
                .getShopPlans(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun createFood(name: String, amount: Double, box: Box, unit: Unit, expirationDate: Date): Observable<Food> {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val body = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("name", name)
                .addFormDataPart("amount", amount.toString())
                .addFormDataPart("box_id", box.id.toString())
                .addFormDataPart("unit_id", unit.id.toString())
                .addFormDataPart("expiration_date", simpleDateFormat.format(expirationDate))
                .build()

        return mRetrofit.create(FoodService::class.java)
                .create(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun update(id: Int, name: String?, amount: Double?, expirationDate: Date?, bitmap: Bitmap?, boxId: Int?, unitId: Int?): Observable<Food> {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val params: HashMap<String, RequestBody> = HashMap()
        var image: MultipartBody.Part? = null

        name?.let { params["name"] = RequestBody.create(MediaType.parse("multipart/form-data"), it) }
        amount?.let { params["amount"] = RequestBody.create(MediaType.parse("multipart/form-data"), it.toString()) }
        expirationDate?.let { params["expiration_date"] = RequestBody.create(MediaType.parse("multipart/form-data"), simpleDateFormat.format(it)) }
        bitmap?.let {
            val imageFile = File(mContext.cacheDir, "temp.jpg")
            val byteArrayOutputStream = ByteArrayOutputStream()

            imageFile.createNewFile()
            it.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)

            val bitmapByteArray = byteArrayOutputStream.toByteArray()
            val fileOutputStream = FileOutputStream(imageFile)

            fileOutputStream.write(bitmapByteArray)
            fileOutputStream.flush()
            fileOutputStream.close()

            image = MultipartBody.Part.createFormData("image", "image.jpg", RequestBody.create(MediaType.parse("image/jpeg"), File(mContext.cacheDir, "temp.jpg")));
        }
        boxId?.let { params["box_id"] = RequestBody.create(MediaType.parse("multipart/form-data"), it.toString()) }
        unitId?.let { params["unit_id"] = RequestBody.create(MediaType.parse("multipart/form-data"), it.toString()) }

        return mRetrofit.create(FoodService::class.java)
                .update(id, params, image)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun removeFood(id: Int): Observable<Void> {
        return mRetrofit.create(FoodService::class.java)
                .remove(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun createNotice(foodId: Int, text: String): Observable<Food> {
        val bodyBuilder = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("text", text)

        return mRetrofit.create(FoodService::class.java)
                .addNotice(foodId, bodyBuilder.build())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }
}