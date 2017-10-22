package com.refrii.client

import java.io.Serializable
import java.util.Date

/**
 * Created by yusuke on 2017/09/01.
 */

class Unit : Serializable {
    val id: Int = 0
    var label: String? = null
    var step: Double = 0.toDouble()
    val createdAt: Date? = null
    val updatedAt: Date? = null

    override fun equals(`object`: Any?): Boolean {
        val unit = `object` as Unit?
        return label == unit!!.label
    }
}
