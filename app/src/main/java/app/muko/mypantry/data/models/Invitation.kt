package app.muko.mypantry.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.RoomWarnings
import java.util.*

@SuppressWarnings(RoomWarnings.PRIMARY_KEY_FROM_EMBEDDED_IS_DROPPED)
@Entity
data class Invitation(
        @PrimaryKey
        val id: Int,
        val box: Box,
        val user: User,
        val createdAt: Date,
        val updatedAt: Date
) {

    override fun equals(other: Any?): Boolean {
        return (other as Invitation).id == id
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + user.hashCode()
        result = 31 * result + createdAt.hashCode()
        result = 31 * result + updatedAt.hashCode()
        return result
    }
}