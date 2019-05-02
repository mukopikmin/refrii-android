package com.refrii.client.data.models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.io.Serializable
import java.util.*

open class ShopPlan : RealmObject(), Serializable {

    @PrimaryKey
    open var id: Int = 0
    open var notice: String? = null
    open var amount: Double = 0.toDouble()
    open var date: Date? = null
    open var done: Boolean = false
    open var createdAt: Date? = null
    open var updatedAt: Date? = null
}
