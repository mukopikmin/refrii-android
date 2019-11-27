package com.refrii.client.data.source

import com.refrii.client.data.models.Box
import com.refrii.client.data.models.Food
import com.refrii.client.data.models.Invitation
import com.refrii.client.data.models.Unit
import com.refrii.client.data.source.local.ApiLocalBoxSource
import com.refrii.client.data.source.local.ApiLocalFoodSource
import com.refrii.client.data.source.local.ApiLocalUnitSource
import com.refrii.client.data.source.remote.ApiRemoteBoxSource
import io.realm.Realm
import retrofit2.Retrofit
import rx.Observable

class ApiBoxRepository(realm: Realm, retrofit: Retrofit) {

    private val mApiRemoteBoxSource = ApiRemoteBoxSource(retrofit)
    private val mApiLocalBoxSource = ApiLocalBoxSource(realm)
    private val mApiLocalUnitSource = ApiLocalUnitSource(realm)
    private val mApiLocalFoodSource = ApiLocalFoodSource(realm)

    fun getBoxes(): Observable<List<Box>> {
        return Observable.zip(
                mApiRemoteBoxSource.getBoxes(),
                mApiLocalBoxSource.getBoxes()
        ) { remote, cache -> Pair(remote, cache) }
                .flatMap { pair ->
                    val remote = pair.first
                    val cache = pair.second

                    remote.forEach { mApiLocalBoxSource.saveBox(it) }

                    cache.forEach { box ->
                        if (!remote.map { it.id }.contains(box.id)) {
                            mApiLocalBoxSource.removeBox(box.id)
                        }
                    }

                    mApiLocalBoxSource.getBoxes()
                }
    }

    fun getBoxesFromCache(): Observable<List<Box>> {
        return mApiLocalBoxSource.getBoxes()
    }

    fun getBox(id: Int): Observable<Box?> {
        return mApiRemoteBoxSource.getBox(id)
                .flatMap { mApiLocalBoxSource.saveBox(it) }
    }

    fun getFoodsInBox(id: Int): Observable<List<Food>> {
        return Observable.zip(
                mApiRemoteBoxSource.getFoodsInBox(id),
                mApiLocalBoxSource.getFoodsInBox(id)
        ) { remoteFoods, cachedFoods -> Pair(remoteFoods, cachedFoods) }
                .flatMap { pair ->
                    val remote = pair.first
                    val cache = pair.second

                    remote.forEach { mApiLocalFoodSource.saveFood(it) }

                    cache.forEach { food ->
                        val ids = remote.map { it.id }

                        if (!ids.contains(food.id)) {
                            mApiLocalFoodSource.removeFood(food.id)
                        }
                    }

                    mApiLocalBoxSource.getFoodsInBox(id)
                }
    }

    fun getFoodsInBoxFromCache(id: Int): Observable<List<Food>> {
        return mApiLocalBoxSource.getFoodsInBox(id)
    }

    fun getBoxFromCache(id: Int): Observable<Box?> {
        return mApiLocalBoxSource.getBox(id)
    }

    fun createBox(name: String, notice: String?): Observable<Box> {
        return mApiRemoteBoxSource.createBox(name, notice)
                .flatMap { mApiLocalBoxSource.saveBox(it) }
    }

    fun getUnitsForBox(id: Int): Observable<List<Unit>> {
        return Observable.zip(
                mApiRemoteBoxSource.getUnitsForBox(id),
                mApiLocalBoxSource.getUnitsForBox(id)
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

                    mApiLocalBoxSource.getUnitsForBox(id)
                }
    }

    fun getUnitsForBoxFromCache(id: Int): Observable<List<Unit>> {
        return mApiLocalBoxSource.getUnitsForBox(id)
    }

    fun updateBox(id: Int, name: String?, notice: String?): Observable<Box> {
        return mApiRemoteBoxSource.updateBox(id, name, notice)
                .flatMap { mApiLocalBoxSource.saveBox(it) }
    }

    fun removeBox(id: Int): Observable<Void> {
        return mApiRemoteBoxSource.removeBox(id)
                .flatMap { mApiLocalBoxSource.removeBox(id) }
    }

    fun invite(boxId: Int, email: String): Observable<Invitation> {
        return mApiRemoteBoxSource.invite(boxId, email)
    }
}