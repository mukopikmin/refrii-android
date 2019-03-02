package com.refrii.client.data.api.source

import com.refrii.client.data.api.models.Box
import com.refrii.client.data.api.models.Credential
import com.refrii.client.data.api.models.Food
import com.refrii.client.data.api.models.Unit
import rx.Observable
import java.util.*

interface ApiDataSource {

    fun auth(googleToken: String, callback: ApiRepositoryCallback<Credential>)

    fun getBoxes(callback: ApiRepositoryCallback<List<Box>>): Observable<List<Box>>
    fun getBox(id: Int, callback: ApiRepositoryCallback<Box>)
    fun updateBox(box: Box, callback: ApiRepositoryCallback<Box>)
    fun getFoodsInBox(id: Int, callback: ApiRepositoryCallback<List<Food>>): Observable<List<Food>>

    fun getFood(id: Int, callback: ApiRepositoryCallback<Food>)
    fun createFood(name: String, notice: String, amount: Double, box: Box, unit: Unit, expirationDate: Date, callback: ApiRepositoryCallback<Food>)
    fun updateFood(food: Food, box: Box, callback: ApiRepositoryCallback<Food>)
    fun removeFood(id: Int, callback: ApiRepositoryCallback<Void>)

    fun getUnits(userId: Int, callback: ApiRepositoryCallback<List<Unit>>)
    fun getUnit(id: Int, callback: ApiRepositoryCallback<Unit>)
    fun createUnit(label: String, step: Double, callback: ApiRepositoryCallback<Unit>)
    fun updateUnit(unit: Unit, callback: ApiRepositoryCallback<Unit>)
    fun removeUnit(id: Int, callback: ApiRepositoryCallback<Void>)
}