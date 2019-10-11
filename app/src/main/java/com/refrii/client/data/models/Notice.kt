package com.refrii.client.data.models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

open class Notice : RealmObject() {

    @PrimaryKey
    open var id: Int = 0
    open var text: String? = null
    open var createdUser: User? = null
    open var updatedUser: User? = null
    open var createdAt: Date? = null
    open var updatedAt: Date? = null
}