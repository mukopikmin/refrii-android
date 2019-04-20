package com.refrii.client.data.models

import io.realm.RealmList
import io.realm.RealmObject
import java.util.*

open class FoodChangeSet : RealmObject() {

    open var name: RealmList<String>? = null
    open var notice: RealmList<String>? = null
    open var amount: RealmList<Double>? = null
    open var expirationDate: RealmList<Date>? = null
    open var updatedAt: RealmList<Date>? = null
}