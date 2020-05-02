package app.muko.mypantry.data.source.remote

import app.muko.mypantry.data.models.Box
import app.muko.mypantry.data.models.Food
import app.muko.mypantry.data.models.Invitation
import app.muko.mypantry.data.models.Unit
import app.muko.mypantry.data.source.remote.services.BoxService
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.MultipartBody
import retrofit2.Retrofit

class ApiRemoteBoxSource(private val mRetrofit: Retrofit) {

    fun getBoxes(): Flowable<List<Box>> {
        return mRetrofit.create(BoxService::class.java)
                .getBoxes()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun getBox(id: Int): Flowable<Box> {
        return mRetrofit.create(BoxService::class.java)
                .getBox(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun createBox(name: String, notice: String?): Flowable<Box> {
        val builder = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("name", name)

        notice?.let { builder.addFormDataPart("notice", notice) }

        return mRetrofit.create(BoxService::class.java)
                .createBox(builder.build())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }


    fun updateBox(id: Int, name: String?, notice: String?): Flowable<Box> {
        val builder = MultipartBody.Builder()
                .setType(MultipartBody.FORM)

        name?.let { builder.addFormDataPart("name", it) }
        notice?.let { builder.addFormDataPart("notice", it) }

        return mRetrofit.create(BoxService::class.java)
                .updateBox(id, builder.build())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun removeBox(id: Int): Flowable<Void> {
        return mRetrofit.create(BoxService::class.java)
                .removeBox(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun getFoodsInBox(id: Int): Flowable<List<Food>> {
        return mRetrofit.create(BoxService::class.java)
                .getFoodsInBox(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun getUnitsForBox(id: Int): Flowable<List<Unit>> {
        return mRetrofit.create(BoxService::class.java)
                .getUnitsForBox(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun invite(boxId: Int, email: String): Flowable<Invitation> {
        val body = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("email", email)
                .build()

        return mRetrofit.create(BoxService::class.java)
                .invite(boxId, body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }
}