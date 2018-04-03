package com.refrii.client.data.api.source

import android.content.Context
import com.refrii.client.data.api.models.Box
import com.refrii.client.data.api.models.Credential
import com.refrii.client.data.api.models.Food
import com.refrii.client.data.api.models.Unit
import com.refrii.client.data.api.source.remote.ApiRemoteDataSource
import retrofit2.Retrofit
import java.util.*

class ApiRepository(context: Context, retrofit: Retrofit) : ApiDataSource {

    private val mApiRemoteDataSource = ApiRemoteDataSource(retrofit)

    override fun auth(googleToken: String, callback: ApiRepositoryCallback<Credential>) {
        mApiRemoteDataSource.auth(googleToken, callback)
    }

    override fun getBoxes(callback: ApiRepositoryCallback<List<Box>>) {
        mApiRemoteDataSource.getBoxes(callback)
    }

    override fun getBox(id: Int, callback: ApiRepositoryCallback<Box>) {
        mApiRemoteDataSource.getBox(id, callback)
    }

    override fun updateBox(box: Box, callback: ApiRepositoryCallback<Box>) {
        mApiRemoteDataSource.updateBox(box, callback)
    }

    override fun getFood(id: Int, callback: ApiRepositoryCallback<Food>) {
        mApiRemoteDataSource.getFood(id, callback)
    }

    override fun createFood(name: String, notice: String, amount: Double, box: Box, unit: Unit, expirationDate: Date, callback: ApiRepositoryCallback<Food>) {
        mApiRemoteDataSource.createFood(name, notice, amount, box, unit, expirationDate, callback)
    }

    override fun updateFood(food: Food, box: Box, callback: ApiRepositoryCallback<Food>) {
        mApiRemoteDataSource.updateFood(food, box, callback)
    }

    override fun removeFood(id: Int, callback: ApiRepositoryCallback<Void>) {
        mApiRemoteDataSource.removeFood(id, callback)
    }

    override fun getUnits(userId: Int, callback: ApiRepositoryCallback<List<Unit>>) {
        mApiRemoteDataSource.getUnits(userId, callback)
    }

    override fun getUnit(id: Int, callback: ApiRepositoryCallback<Unit>) {
        mApiRemoteDataSource.getUnit(id, callback)
    }

    override fun createUnit(label: String, step: Double, callback: ApiRepositoryCallback<Unit>) {
        mApiRemoteDataSource.createUnit(label, step, callback)
    }

    override fun updateUnit(unit: Unit, callback: ApiRepositoryCallback<Unit>) {
        mApiRemoteDataSource.updateUnit(unit, callback)
    }

    override fun removeUnit(id: Int, callback: ApiRepositoryCallback<Void>) {
        mApiRemoteDataSource.removeUnit(id, callback)
    }
}