package com.refrii.client

import java.io.Serializable
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date

class Box(val id: Int) : Serializable {
    var name: String? = null
    var notice: String? = null
    val imageUrl: String? = null
    val isInvited: Boolean = false
    val updatedAt: Date? = null
    val createdAt: Date? = null
    val foods: MutableList<Food>? = null
    val invitedUsers: List<User>? = null
    val owner: User? = null

    override fun equals(`object`: Any?): Boolean {
        val box = `object` as Box?
        return box!!.id == id
    }
}
