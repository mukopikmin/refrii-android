package com.refrii.client.data.api.models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.io.Serializable
import java.util.*

open class User : RealmObject(), Serializable {
    @PrimaryKey
    open var id: Int = 0
    open var name: String? = null
    open var email: String? = null
    open var provider: String? = null
    open var avatarUrl: String? = null
    open var updatedAt: Date? = null
    open var createdAt: Date? = null
}
