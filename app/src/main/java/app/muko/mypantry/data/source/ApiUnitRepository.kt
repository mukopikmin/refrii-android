package app.muko.mypantry.data.source

import app.muko.mypantry.data.models.Unit
import app.muko.mypantry.data.source.remote.ApiRemoteUnitSource
import retrofit2.Retrofit
import rx.Observable

class ApiUnitRepository(retrofit: Retrofit) {

    private val mApiRemoteUnitSource = ApiRemoteUnitSource(retrofit)

    fun getUnits(userId: Int): Observable<List<Unit>> {
        return mApiRemoteUnitSource.getUnits()
    }

    fun getUnit(id: Int): Observable<Unit> {
        return mApiRemoteUnitSource.getUnit(id)
    }

    fun createUnit(label: String, step: Double): Observable<Unit> {
        return mApiRemoteUnitSource.createUnit(label, step)
    }

    fun updateUnit(id: Int, label: String?, step: Double?): Observable<Unit> {
        return mApiRemoteUnitSource.updateUnit(id, label, step)
    }

    fun removeUnit(id: Int): Observable<Void> {
        return mApiRemoteUnitSource.removeUnit(id)
    }
}