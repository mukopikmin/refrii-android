package app.muko.mypantry.data.dao

import androidx.room.TypeConverter
import app.muko.mypantry.data.models.Box
import app.muko.mypantry.data.models.Invitation
import app.muko.mypantry.data.models.Notice
import com.google.gson.Gson
import java.util.*

class Converters {
    private val separator = "__,__"

    @TypeConverter
    fun dateToLong(date: Date): Long {
        return date.time
    }

    @TypeConverter
    fun longToDate(date: Long): Date {
        return Date(date)
    }

    @TypeConverter
    fun boxListToString(list: List<Box>): String {
        val gson = Gson()

        return list.joinToString(separator) { gson.toJson(it) }
    }

    @TypeConverter
    fun stringToBoxList(str: String): List<Box> {
        val gson = Gson()

        return str.split(separator)
                .map { gson.fromJson(it, Box::class.java) }
    }

    @TypeConverter
    fun noticeListToString(list: List<Notice>): String {
        val gson = Gson()

        return list.joinToString(separator) { gson.toJson(it) }
    }

    @TypeConverter
    fun stringToNoticeList(str: String): List<Notice> {
        val gson = Gson()

        return str.split(separator)
                .map { gson.fromJson(it, Notice::class.java) }
    }

    @TypeConverter
    fun stringToInvitationList(str: String): List<Invitation> {
        val gson = Gson()

        return str.split(separator)
                .map { gson.fromJson(it, Invitation::class.java) }
    }

    @TypeConverter
    fun invitationListToString(list: List<Invitation>): String {
        val gson = Gson()

        return list.joinToString(separator) { gson.toJson(it) }
    }

    @TypeConverter
    fun stringListToString(list: List<String>): String {
        val gson = Gson()

        return list.joinToString(separator) { gson.toJson(it) }
    }

    @TypeConverter
    fun stringToStringList(str: String): List<String> {
        val gson = Gson()

        return str.split(separator)
                .map { gson.fromJson(it, String::class.java) }
    }
}
