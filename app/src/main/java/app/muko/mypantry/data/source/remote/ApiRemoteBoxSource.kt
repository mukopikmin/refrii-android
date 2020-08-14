package app.muko.mypantry.data.source.remote

import app.muko.mypantry.data.models.Box
import app.muko.mypantry.data.source.data.ApiBoxDataSource
import app.muko.mypantry.data.source.remote.services.BoxService
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.MultipartBody

class ApiRemoteBoxSource(
        private val service: BoxService
) : ApiBoxDataSource {

//    fun getBoxes(): Flowable<List<Box>> {
//        return service.getAll()
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//    }
//
//    fun getBox(id: Int): Flowable<Box> {
//        return service.get(id)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//    }
//
//    fun createBox(name: String, notice: String?): Flowable<Box> {
//        val builder = MultipartBody.Builder()
//                .setType(MultipartBody.FORM)
//                .addFormDataPart("name", name)
//
//        notice?.let { builder.addFormDataPart("notice", notice) }
//
//        return service.create(builder.build())
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//    }
//
//
//    fun updateBox(id: Int, name: String?, notice: String?): Flowable<Box> {
//        val builder = MultipartBody.Builder()
//                .setType(MultipartBody.FORM)
//
//        name?.let { builder.addFormDataPart("name", it) }
//        notice?.let { builder.addFormDataPart("notice", it) }
//
//        return service.update(id, builder.build())
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//    }
//
//    fun removeBox(id: Int): Flowable<Void> {
//        return service.remove(id)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//    }
//
//    fun getFoodsInBox(id: Int): Flowable<List<Food>> {
//        return service.getFoodsInBox(id)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//    }
//
//    fun getUnitsForBox(id: Int): Flowable<List<Unit>> {
//        return service.getUnitsForBox(id)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//    }
//
//    fun invite(boxId: Int, email: String): Flowable<Invitation> {
//        val body = MultipartBody.Builder()
//                .setType(MultipartBody.FORM)
//                .addFormDataPart("email", email)
//                .build()
//
//        return service.invite(boxId, body)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//    }

    override fun getAll(): Flowable<List<Box>> {
        return service.getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    override fun get(id: Int): Flowable<Box?> {
        return service.get(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    override fun create(box: Box): Completable {
        val builder = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("name", box.name)

        box.notice?.let { builder.addFormDataPart("notice", it) }

        return service.create(builder.build())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    override fun update(box: Box): Completable {
        val builder = MultipartBody.Builder()
                .setType(MultipartBody.FORM)

        builder.addFormDataPart("name", box.name)
        box.notice?.let { builder.addFormDataPart("notice", it) }

        return service.update(box.id, builder.build())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    override fun remove(box: Box): Completable {
        return service.remove(box.id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }
}