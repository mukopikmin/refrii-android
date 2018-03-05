package com.refrii.client.models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import okhttp3.MultipartBody
import java.io.Serializable
import java.util.*

open class Unit : RealmObject(), Serializable {

    @PrimaryKey
    open var id: Int = 0
    open var label: String? = null
    open var step: Double = 0.toDouble()
    open var createdAt: Date? = null
    open var updatedAt: Date? = null
    open var user: User? = null

    override fun equals(other: Any?): Boolean {
        other ?: return false

        val unit = other as Unit

        return label == unit.label
    }

    fun toMultipartBody(): MultipartBody {
        val builder = MultipartBody.Builder().setType(MultipartBody.FORM)

        label?.let { builder.addFormDataPart("label", it) }
        builder.addFormDataPart("step", step.toString())

        return builder.build()
    }
}
