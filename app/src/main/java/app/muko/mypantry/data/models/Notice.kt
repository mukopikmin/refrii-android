package app.muko.mypantry.data.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Notice(
        @PrimaryKey
        val id: Int,
        val text: String,
        @Embedded(prefix = "created_user_")
        val createdUser: User,
        @Embedded(prefix = "updated_user_")
        val updatedUser: User,
        val createdAt: Date,
        val updatedAt: Date
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
