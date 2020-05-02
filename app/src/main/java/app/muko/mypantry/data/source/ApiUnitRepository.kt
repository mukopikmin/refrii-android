package app.muko.mypantry.data.source

import app.muko.mypantry.data.models.Unit
import app.muko.mypantry.data.source.remote.ApiRemoteUnitSource
import io.reactivex.Flowable
import retrofit2.Retrofit

class ApiUnitRepository(retrofit: Retrofit) {

    private val mApiRemoteUnitSource = ApiRemoteUnitSource(retrofit)

    fun getUnits(userId: Int): Flowable<List<Unit>> {
        return mApiRemoteUnitSource.getUnits()
    }

    fun getUnit(id: Int): Flowable<Unit> {
        return mApiRemoteUnitSource.getUnit(id)
    }

    fun createUnit(label: String, step: Double): Flowable<Unit> {
        return mApiRemoteUnitSource.createUnit(label, step)
    }

    fun updateUnit(id: Int, label: String?, step: Double?): Flowable<Unit> {
        return mApiRemoteUnitSource.updateUnit(id, label, step)
    }

    fun removeUnit(id: Int): Flowable<Void> {
        return mApiRemoteUnitSource.removeUnit(id)
    }
}