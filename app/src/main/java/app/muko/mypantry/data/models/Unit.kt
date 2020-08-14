package app.muko.mypantry.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.RoomWarnings
import java.util.*

@SuppressWarnings(RoomWarnings.PRIMARY_KEY_FROM_EMBEDDED_IS_DROPPED)
@Entity
data class Unit(
        @PrimaryKey
        val id: Int,
        val label: String,
        val step: Double,
        val createdAt: Date,
        val updatedAt: Date,
        val user: User?
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

    companion object {
        fun temp(label: String, step: Double): Unit {
            val dummyTimestamp = Date()

            return Unit(-1, label, step, dummyTimestamp, dummyTimestamp, null)
        }
    }
}