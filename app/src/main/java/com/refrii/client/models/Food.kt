package com.refrii.client.models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import okhttp3.MultipartBody

import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

open class Food : RealmObject(), Serializable, Comparable<Food> {
    @PrimaryKey
    open var id: Int = 0
    open var name: String? = null
    open var notice: String? = null
    open var amount: Double = 0.toDouble()
    open var expirationDate: Date? = null
    open var isNeedsAdding: Boolean = false
    open var imageUrl: String? = null
    open var createdAt: Date? = null
    open var updatedAt: Date? = null
    open var unit: Unit? = null
    open var createdUser: User? = null
    open var updatedUser: User? = null
    open var box: Box? = null

    override fun equals(other: Any?): Boolean {
        other ?: return false

        val food = other as Food
        return this.id == food.id
    }

    fun decrease(diff: Double) {
        this.amount -= diff
        if (this.amount < 0) {
            this.amount = 0.0
        }
    }

    fun increase(diff: Double) {
        this.amount += diff
    }

    override fun compareTo(other: Food): Int =
            (this.expirationDate!!.time - other.expirationDate!!.time).toInt()
}
