package com.refrii.client

import android.view.Menu
import android.widget.Toast

import java.io.Serializable
import java.util.Date

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by yusuke on 2017/09/01.
 */

class Food : Serializable, Comparable<Food> {
    val id: Int = 0
    var name: String? = null
    var notice: String? = null
    var amount: Double = 0.toDouble()
    var expirationDate: Date? = null
    val isNeedsAdding: Boolean = false
    val imageUrl: String? = null
    val createdAt: Date? = null
    val updatedAt: Date? = null
    val unit: Unit? = null
    val createdUser: User? = null
    val updatedUser: User? = null
    val box: Box? = null

    override fun equals(obj: Any?): Boolean {
        val food = obj as Food?
        return this.id == food!!.id
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

    override fun compareTo(food: Food): Int {
        return (this.expirationDate!!.time - food.expirationDate!!.time).toInt()
    }
}
