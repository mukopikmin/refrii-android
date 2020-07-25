package app.muko.mypantry.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Box(
        @PrimaryKey
        val id: Int,
        val name: String,
        val notice: String?,
        val imageUrl: String?,
        val isInvited: Boolean,
        val updatedAt: Date,
        val createdAt: Date,
        val invitations: List<Invitation>,
        val owner: User
) {

    override fun equals(other: Any?): Boolean {
        return (other as Box).id == id
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + name.hashCode()
        result = 31 * result + notice.hashCode()
        result = 31 * result + imageUrl.hashCode()
        result = 31 * result + isInvited.hashCode()
        result = 31 * result + updatedAt.hashCode()
        result = 31 * result + createdAt.hashCode()
        result = 31 * result + invitations.hashCode()
        result = 31 * result + owner.hashCode()
        return result
    }
}