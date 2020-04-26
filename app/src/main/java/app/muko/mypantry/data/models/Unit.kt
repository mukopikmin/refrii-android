package app.muko.mypantry.data.models

import okhttp3.MultipartBody
import java.io.Serializable
import java.util.*

open class Unit  {

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

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + (label?.hashCode() ?: 0)
        result = 31 * result + step.hashCode()
        result = 31 * result + (createdAt?.hashCode() ?: 0)
        result = 31 * result + (updatedAt?.hashCode() ?: 0)
        result = 31 * result + (user?.hashCode() ?: 0)
        return result
    }
}
