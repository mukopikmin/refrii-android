package app.muko.mypantry.data.dao

import androidx.room.TypeConverter
import app.muko.mypantry.data.models.*
import app.muko.mypantry.data.models.Unit
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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
    fun noticeListToString(list: List<Notice>): String {
        val gson = Gson()
        val type = object : TypeToken<List<Notice>>() {}.type

        return gson.toJson(list, type)
    }

    @TypeConverter
    fun stringToNoticeList(str: String): List<Notice> {
        val gson = Gson()
        val type = object : TypeToken<List<Notice>>() {}.type

        return gson.fromJson(str, type)
    }

    @TypeConverter
    fun invitationListToString(list: List<Invitation>): String {
        val gson = Gson()
        val type = object : TypeToken<List<Invitation>>() {}.type

        return gson.toJson(list, type)
    }

    @TypeConverter
    fun stringToInvitationList(str: String): List<Invitation> {
        val gson = Gson()
        val type = object : TypeToken<List<Invitation>>() {}.type

        return gson.fromJson(str, type)
    }

//    @TypeConverter
//    fun stringListToString(list: List<String>): String {
//        val gson = Gson()
//        val type = object : TypeToken<List<String>>() {}.type
//
//        return gson.toJson(list, type)
//    }
//
//    @TypeConverter
//    fun stringToStringList(str: String): List<String> {
//        val gson = Gson()
//        val type = object : TypeToken<List<String>>() {}.type
//
//        return gson.fromJson(str, type)
//    }

    @TypeConverter
    fun unitToString(unit: Unit): String {
        val gson = Gson()

        return gson.toJson(unit)
    }

    @TypeConverter
    fun stringToUnit(str: String): Unit {
        val gson = Gson()

        return gson.fromJson(str, Unit::class.java)
    }

    @TypeConverter
    fun boxToString(box: Box): String {
        val gson = Gson()

        return gson.toJson(box)
    }

    @TypeConverter
    fun stringToBox(str: String): Box {
        val gson = Gson()

        return gson.fromJson(str, Box::class.java)
    }

    @TypeConverter
    fun userToString(user: User?): String? {
        val gson = Gson()

        return gson.toJson(user)
    }

    @TypeConverter
    fun stringToUser(str: String?): User? {
        val gson = Gson()

        return gson.fromJson(str, User::class.java)
    }

    @TypeConverter
    fun shopPlanListToString(list: List<ShopPlan>): String {
        val gson = Gson()
        val type = object : TypeToken<List<ShopPlan>>() {}.type

        return gson.toJson(list, type)
    }

    @TypeConverter
    fun stringToShopPlanList(str: String): List<ShopPlan> {
        val gson = Gson()
        val type = object : TypeToken<List<ShopPlan>>() {}.type

        return gson.fromJson(str, type)
    }

//    @TypeConverter
//    fun foodToString(food: Food): String {
//        val gson = Gson()
//
//        return gson.toJson(food)
//    }
//
//    @TypeConverter
//    fun stringToFood(str: String): Food {
//        val gson = Gson()
//
//        return gson.fromJson(str, Food::class.java)
//    }
}
