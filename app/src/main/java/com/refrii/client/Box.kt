package com.refrii.client

import android.content.Context
import retrofit2.Call
import retrofit2.Response
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
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

    override fun equals(obj: Any?): Boolean {
        obj ?: return false

        val box = obj as Box
        return box.id == id
    }

    companion object {
        fun getAll(context: Context): Observable<List<Box>> {
            return RetrofitFactory.getClient(BoxService::class.java, context).getBoxes()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
        }
    }
}
