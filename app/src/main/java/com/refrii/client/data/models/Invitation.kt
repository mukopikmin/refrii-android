package com.refrii.client.data.models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

open class Invitation : RealmObject() {

    @PrimaryKey
    open var id: Int = 0
    open var box: Box? = null
    open var user: User? = null
    open var createdAt: Date? = null
    open var updatedAt: Date? = null
}