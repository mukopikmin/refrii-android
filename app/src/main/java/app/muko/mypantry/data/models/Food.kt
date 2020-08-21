package app.muko.mypantry.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Food(
        @PrimaryKey
        val id: Int,
        var name: String,
        val notices: List<Notice>,
        var amount: Double,
        var expirationDate: Date,
        val imageUrl: String?,
        val createdAt: Date,
        val updatedAt: Date,
        var unit: Unit,
        val createdUser: User?,
        val updatedUser: User?,
        val box: Box
) {
    override fun equals(other: Any?): Boolean {
        return (other as Food).id == id
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + name.hashCode()
        result = 31 * result + notices.hashCode()
        result = 31 * result + amount.hashCode()
        result = 31 * result + expirationDate.hashCode()
        result = 31 * result + (imageUrl?.hashCode() ?: 0)
        result = 31 * result + createdAt.hashCode()
        result = 31 * result + updatedAt.hashCode()
        result = 31 * result + unit.hashCode()
        result = 31 * result + createdUser.hashCode()
        result = 31 * result + updatedUser.hashCode()
        result = 31 * result + box.hashCode()
        return result
    }

    companion object {
        fun temp(name: String, amount: Double, expirationDate: Date, unit: Unit, box: Box): Food {
            val dummyTimestamp = Date()

            return Food(
                    -1,
                    name,
                    listOf(),
                    amount,
                    expirationDate,
                    null,
                    dummyTimestamp,
                    dummyTimestamp,
                    unit,
                    null,
                    null,
                    box
            )
        }
    }
}

