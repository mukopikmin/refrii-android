package com.refrii.client.models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

import java.io.Serializable
import java.util.Date

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
