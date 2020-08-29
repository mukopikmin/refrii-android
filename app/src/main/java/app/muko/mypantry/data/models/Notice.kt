package app.muko.mypantry.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.RoomWarnings
import java.util.*

@SuppressWarnings(RoomWarnings.PRIMARY_KEY_FROM_EMBEDDED_IS_DROPPED)
@Entity
data class Notice(
        @PrimaryKey
        val id: Int,
        val text: String,
        val createdUser: User?,
        val updatedUser: User?,
        val createdAt: Date?,
        val updatedAt: Date?
) {
    override fun equals(other: Any?): Boolean {
        return (other as Notice).id == id
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + text.hashCode()
        result = 31 * result + createdUser.hashCode()
        result = 31 * result + updatedUser.hashCode()
        result = 31 * result + createdAt.hashCode()
        result = 31 * result + updatedAt.hashCode()
        return result
    }
}
