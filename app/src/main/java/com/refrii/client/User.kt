package com.refrii.client

import android.util.Log

import java.io.Serializable
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date

class User : Serializable {
    val id: Int = 0
    val name: String? = null
    val email: String? = null
    val provider: String? = null
    val avatarUrl: String? = null
    val updatedAt: Date? = null
    val createdAt: Date? = null
}
