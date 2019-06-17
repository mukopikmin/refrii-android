package com.refrii.client.data.source

import com.refrii.client.data.models.Unit
import com.refrii.client.data.source.local.ApiLocalUnitSource
import com.refrii.client.data.source.remote.ApiRemoteUnitSource
import io.realm.Realm
import retrofit2.Retrofit
import rx.Observable

class ApiUnitRepository(realm: Realm, retrofit: Retrofit) {

    private val mApiRemoteUnitSource = ApiRemoteUnitSource(retrofit)
    private val mApiLocalUnitSource = ApiLocalUnitSource(realm)

    fun getUnits(userId: Int): Observable<List<Unit>> {
        return Observable.zip(
                mApiRemoteUnitSource.getUnits(),
                mApiLocalUnitSource.getUnits(userId)
        ) { remote, cache -> Pair(remote, cache) }
                .flatMap { pair ->
                    val remote = pair.first
                    val cache = pair.second

                    remote.forEach { mApiLocalUnitSource.saveUnit(it) }

                    cache.forEach { unit ->
                        if (!remote.map { it.id }.contains(unit.id)) {
                            mApiLocalUnitSource.removeUnit(unit.id)
                        }
                    }

                    mApiLocalUnitSource.getUnits(userId)
                }
    }

    fun getUnit(id: Int): Observable<Unit?> {
        return mApiRemoteUnitSource.getUnit(id)
                .flatMap { mApiLocalUnitSource.saveUnit(it) }
    }

    fun createUnit(label: String, step: Double): Observable<Unit> {
        return mApiRemoteUnitSource.createUnit(label, step)
                .flatMap { mApiLocalUnitSource.saveUnit(it) }
    }

    fun updateUnit(unit: Unit): Observable<Unit> {
        return mApiRemoteUnitSource.updateUnit(unit)
                .flatMap { mApiLocalUnitSource.saveUnit(it) }
    }

    fun removeUnit(id: Int): Observable<Void> {
        return mApiRemoteUnitSource.removeUnit(id)
    }
}