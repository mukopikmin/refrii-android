package com.refrii.client.data.source

import com.refrii.client.data.models.Box
import com.refrii.client.data.models.Food
import com.refrii.client.data.models.Unit
import rx.Observable
import java.util.*

interface ApiDataSource {

    fun getBoxes(): Observable<List<Box>>
    fun getBox(id: Int, callback: ApiRepositoryCallback<Box>)
    fun updateBox(box: Box, callback: ApiRepositoryCallback<Box>)
    fun getFoodsInBox(id: Int, callback: ApiRepositoryCallback<List<Food>>): Observable<List<Food>>

    fun getFood(id: Int, callback: ApiRepositoryCallback<Food>)
    fun createFood(name: String, notice: String, amount: Double, box: Box, unit: Unit, expirationDate: Date, callback: ApiRepositoryCallback<Food>)
    fun updateFood(food: Food, box: Box, callback: ApiRepositoryCallback<Food>)
    fun removeFood(id: Int, callback: ApiRepositoryCallback<Void>)

    fun getUnits(userId: Int, callback: ApiRepositoryCallback<List<Unit>>): List<Unit>
    fun getUnit(id: Int, callback: ApiRepositoryCallback<Unit>): Unit
    fun createUnit(label: String, step: Double, callback: ApiRepositoryCallback<Unit>)
    fun updateUnit(unit: Unit, callback: ApiRepositoryCallback<Unit>)
    fun removeUnit(id: Int, callback: ApiRepositoryCallback<Void>)
}