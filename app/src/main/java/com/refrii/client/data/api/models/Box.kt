package com.refrii.client.data.api.models

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import okhttp3.MultipartBody
import java.io.Serializable
import java.util.*

open class Box : RealmObject(), Serializable {

    @PrimaryKey
    open var id: Int = 0
    open var name: String? = null
    open var notice: String? = null
    open var imageUrl: String? = null
    open var isInvited: Boolean = false
    open var updatedAt: Date? = null
    open var createdAt: Date? = null
    open var foods: RealmList<Food>? = null
    open var invitedUsers: RealmList<User>? = null
    open var owner: User? = null
    open var changeSets: RealmList<BoxChangeSet>? = null

    override fun equals(other: Any?): Boolean {
        other ?: return false

        val box = other as Box

        return box.id == id
    }

    override fun hashCode(): Int {
        var result = id

        result = 31 * result + (name?.hashCode() ?: 0)
        result = 31 * result + (notice?.hashCode() ?: 0)
        result = 31 * result + (imageUrl?.hashCode() ?: 0)
        result = 31 * result + isInvited.hashCode()
        result = 31 * result + (updatedAt?.hashCode() ?: 0)
        result = 31 * result + (createdAt?.hashCode() ?: 0)
        result = 31 * result + (foods?.hashCode() ?: 0)
        result = 31 * result + (invitedUsers?.hashCode() ?: 0)
        result = 31 * result + (owner?.hashCode() ?: 0)

        return result
    }

    private fun sync(other: Box): Box {
        updatedAt?.let {
            if (it < other.updatedAt) {
                return other
            }
        }
        return this
    }

    fun toMultipartBody(): MultipartBody {
        val builder = MultipartBody.Builder().setType(MultipartBody.FORM)

        builder.addFormDataPart("id", id.toString())
        builder.addFormDataPart("is_invited", isInvited.toString())
        name?.let { builder.addFormDataPart("name", it) }
        notice?.let { builder.addFormDataPart("notice", it) }
        imageUrl?.let { builder.addFormDataPart("imageUrl", it) }

        return builder.build()
    }

    companion object {
        val TAG = "Box"
    }
}
