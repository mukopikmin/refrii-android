package app.muko.mypantry.data.source.remote

import app.muko.mypantry.data.models.Unit
import app.muko.mypantry.data.source.remote.services.UnitService
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.MultipartBody
import retrofit2.Retrofit

class ApiRemoteUnitSource(private val mRetrofit: Retrofit) {

    fun getUnits(): Flowable<List<Unit>> {
        return mRetrofit.create(UnitService::class.java)
                .getUnits()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun getUnit(id: Int): Flowable<Unit> {
        return mRetrofit.create(UnitService::class.java)
                .getUnit(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun createUnit(label: String, step: Double): Flowable<Unit> {
        val body = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("label", label)
                .addFormDataPart("step", step.toString())
                .build()

        return mRetrofit.create(UnitService::class.java)
                .createUnit(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun updateUnit(id: Int, label: String?, step: Double?): Flowable<Unit> {
        val builder = MultipartBody.Builder()
                .setType(MultipartBody.FORM)

        label?.let { builder.addFormDataPart("label", it) }
        step?.let { builder.addFormDataPart("step", it.toString()) }

        return mRetrofit.create(UnitService::class.java)
                .updateUnit(id, builder.build())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun removeUnit(id: Int): Flowable<Void> {
        return mRetrofit.create(UnitService::class.java)
                .deleteUnit(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }
}