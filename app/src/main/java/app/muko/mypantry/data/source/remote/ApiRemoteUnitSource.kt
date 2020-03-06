package app.muko.mypantry.data.source.remote

import app.muko.mypantry.data.models.Unit
import app.muko.mypantry.data.source.remote.services.UnitService
import okhttp3.MultipartBody
import retrofit2.Retrofit
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class ApiRemoteUnitSource(private val mRetrofit: Retrofit) {

    fun getUnits(): Observable<List<Unit>> {
        return mRetrofit.create(UnitService::class.java)
                .getUnits()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun getUnit(id: Int): Observable<Unit> {
        return mRetrofit.create(UnitService::class.java)
                .getUnit(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun createUnit(label: String, step: Double): Observable<Unit> {
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

    fun updateUnit(id: Int, label: String?, step: Double?): Observable<Unit> {
        val builder = MultipartBody.Builder()
                .setType(MultipartBody.FORM)

        label?.let { builder.addFormDataPart("label", it) }
        step?.let { builder.addFormDataPart("step", it.toString()) }

        return mRetrofit.create(UnitService::class.java)
                .updateUnit(id, builder.build())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun removeUnit(id: Int): Observable<Void> {
        return mRetrofit.create(UnitService::class.java)
                .deleteUnit(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }
}