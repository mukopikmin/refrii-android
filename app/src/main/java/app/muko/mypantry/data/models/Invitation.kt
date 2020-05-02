package app.muko.mypantry.data.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Invitation(
        @PrimaryKey
        val id: Int,
        @Embedded(prefix = "user_")
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