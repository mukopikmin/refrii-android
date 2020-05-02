package app.muko.mypantry.data.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Unit(
        @PrimaryKey
        val id: Int,
        val label: String,
        val step: Double,
        val createdAt: Date,
        val updatedAt: Date,
        @Embedded(prefix = "user_")
        val user: User
) {
    override fun equals(other: Any?): Boolean {
        return (other as Unit).id == id
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + label.hashCode()
        result = 31 * result + step.hashCode()
        result = 31 * result + createdAt.hashCode()
        result = 31 * result + updatedAt.hashCode()
        result = 31 * result + user.hashCode()
        return result
    }
}