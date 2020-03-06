package app.muko.mypantry.data.source.remote

import app.muko.mypantry.data.models.ShopPlan
import app.muko.mypantry.data.source.remote.services.ShopPlanService
import okhttp3.MultipartBody
import retrofit2.Retrofit
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*

class ApiRemoteShopPlanSource(private val mRetrofit: Retrofit) {
    fun getShopPlans(): Observable<List<ShopPlan>> {
        return mRetrofit.create(ShopPlanService::class.java)
                .getShopPlans()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun createShopPlan(foodId: Int, amount: Double, date: Date): Observable<ShopPlan> {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val body = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("food_id", foodId.toString())
                .addFormDataPart("amount", amount.toString())
                .addFormDataPart("date", formatter.format(date))
                .build()

        return mRetrofit.create(ShopPlanService::class.java)
                .createShopPlans(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun updateShopPlan(id: Int, done: Boolean?): Observable<ShopPlan> {
        val body = MultipartBody.Builder()
                .setType(MultipartBody.FORM)

        done?.let { body.addFormDataPart("done", it.toString()) }

        return mRetrofit.create(ShopPlanService::class.java)
                .updateShopPlan(id, body.build())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

}