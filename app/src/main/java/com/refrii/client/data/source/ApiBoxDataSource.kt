package com.refrii.client.data.source

import com.refrii.client.data.models.Box
import com.refrii.client.data.models.Food
import com.refrii.client.data.models.Invitation
import com.refrii.client.data.models.Unit
import rx.Observable

interface ApiBoxDataSource {

    fun getBoxes(): Observable<List<Box>>
    fun getBoxesFromCache(): Observable<List<Box>>
    fun getBox(id: Int): Observable<Box?>
    fun getFoodsInBox(id: Int): Observable<List<Food>>
    fun getFoodsInBoxFromCache(id: Int): Observable<List<Food>>
    fun getBoxFromCache(id: Int): Observable<Box?>
    fun createBox(name: String, notice: String?): Observable<Box>
    fun getUnitsForBox(id: Int): Observable<List<Unit>>
    fun getUnitsForBoxFromCache(id: Int): Observable<List<Unit>>
    fun updateBox(id: Int, name: String?, notice: String?): Observable<Box>
    fun removeBox(id: Int): Observable<Void>
    fun invite(boxId: Int, email: String): Observable<Invitation>
    fun uninvite(boxId: Int, email: String): Observable<Void>
}