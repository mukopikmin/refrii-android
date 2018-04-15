package com.refrii.client.data.api.source.local

import android.content.Context
import com.refrii.client.data.api.models.Box
import com.refrii.client.data.api.models.Credential
import com.refrii.client.data.api.models.Food
import com.refrii.client.data.api.models.Unit
import com.refrii.client.data.api.source.ApiDataSource
import com.refrii.client.data.api.source.ApiRepositoryCallback
import io.realm.Realm
import java.util.*

class ApiLocalDataSource(context: Context) : ApiDataSource {

    private var mRealm: Realm

    init {
        Realm.init(context)
        mRealm = RealmUtil.getInstance()
    }

    override fun auth(googleToken: String, callback: ApiRepositoryCallback<Credential>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getBoxes(callback: ApiRepositoryCallback<List<Box>>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getBox(id: Int, callback: ApiRepositoryCallback<Box>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun updateBox(box: Box, callback: ApiRepositoryCallback<Box>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getFood(id: Int, callback: ApiRepositoryCallback<Food>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun createFood(name: String, notice: String, amount: Double, box: Box, unit: Unit, expirationDate: Date, callback: ApiRepositoryCallback<Food>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun updateFood(food: Food, box: Box, callback: ApiRepositoryCallback<Food>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun removeFood(id: Int, callback: ApiRepositoryCallback<Void>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getUnits(userId: Int, callback: ApiRepositoryCallback<List<Unit>>) {
        val units = mRealm.where(Unit::class.java)
                .equalTo("user.id", userId)
                .or()
                .isNull("user")
                .findAll()
                .sort("id")

        if (units == null) {
            callback.onError(Exception("No units"))
        } else {
            callback.onNext(units)
            callback.onCompleted()
        }
    }

    override fun getUnit(id: Int, callback: ApiRepositoryCallback<Unit>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun createUnit(label: String, step: Double, callback: ApiRepositoryCallback<Unit>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun updateUnit(unit: Unit, callback: ApiRepositoryCallback<Unit>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun removeUnit(id: Int, callback: ApiRepositoryCallback<Void>) {
        mRealm.executeTransaction {
            it.where(Unit::class.java)
                    .equalTo("id", id)
                    .findFirst()
                    ?.deleteFromRealm()
        }
    }
}