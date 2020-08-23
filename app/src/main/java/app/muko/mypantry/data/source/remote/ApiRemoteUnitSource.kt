package app.muko.mypantry.data.source.remote

import app.muko.mypantry.data.models.Box
import app.muko.mypantry.data.models.Unit
import app.muko.mypantry.data.source.data.ApiUnitDataSource
import app.muko.mypantry.data.source.remote.services.UnitService
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.MultipartBody

class ApiRemoteUnitSource(
        private val service: UnitService
) : ApiUnitDataSource {

    override fun getAll(): Flowable<List<Unit>> {
        return service.getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    override fun getByBox(box: Box): Flowable<List<Unit>> {
        return service.getByBox(box.id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    override fun get(id: Int): Flowable<Unit?> {
        return service.get(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    override fun create(unit: Unit): Completable {
        val body = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("label", unit.label)
                .addFormDataPart("step", unit.step.toString())
                .build()

        return service.create(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    override fun update(unit: Unit): Completable {
        val builder = MultipartBody.Builder()
                .setType(MultipartBody.FORM)

        unit.label?.let { builder.addFormDataPart("label", it) }
        unit.step?.let { builder.addFormDataPart("step", it.toString()) }

        return service.update(unit.id, builder.build())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    override fun remove(unit: Unit): Completable {
        return service.remove(unit.id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }
}