package app.muko.mypantry.data.models

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
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
    open var invitations: RealmList<Invitation>? = null
    open var owner: User? = null

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
        result = 31 * result + (invitations?.hashCode() ?: 0)
        result = 31 * result + (owner?.hashCode() ?: 0)

        return result
    }
}
