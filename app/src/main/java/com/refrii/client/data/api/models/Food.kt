package com.refrii.client.data.api.models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.io.Serializable
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

    override fun compareTo(other: Food): Int =
            (this.expirationDate!!.time - other.expirationDate!!.time).toInt()

    fun daysLeft(): Long {
        expirationDate?.let {
            return (it.time - Date().time) / (24 * 60 * 60 * 1000)
        }

        return 0
    }

    fun decrease(): Food {
        this.unit?.let {
            this.amount -= it.step
        }

        if (this.amount < 0) {
            this.amount = 0.0
        }

        return this
    }

    fun increase(): Food {
        this.unit?.let {
            this.amount += it.step
        }

        return this
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + (name?.hashCode() ?: 0)
        result = 31 * result + (notice?.hashCode() ?: 0)
        result = 31 * result + amount.hashCode()
        result = 31 * result + (expirationDate?.hashCode() ?: 0)
        result = 31 * result + isNeedsAdding.hashCode()
        result = 31 * result + (imageUrl?.hashCode() ?: 0)
        result = 31 * result + (createdAt?.hashCode() ?: 0)
        result = 31 * result + (updatedAt?.hashCode() ?: 0)
        result = 31 * result + (unit?.hashCode() ?: 0)
        result = 31 * result + (createdUser?.hashCode() ?: 0)
        result = 31 * result + (updatedUser?.hashCode() ?: 0)
        return result
    }

    companion object {
        @Suppress("unused")
        private const val TAG = "Food"
    }
}
