package app.muko.mypantry.data.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.RoomWarnings
import java.util.*

@SuppressWarnings(RoomWarnings.PRIMARY_KEY_FROM_EMBEDDED_IS_DROPPED)
@Entity
data class ShopPlan(
        @PrimaryKey
        val id: Int,
        val notice: String,
        val amount: Double,
        val date: Date,
        val done: Boolean,
        val createdAt: Date,
        val updatedAt: Date,
        @Embedded(prefix = "food_")
        val food: Food
) {
    override fun equals(other: Any?): Boolean {
        return (other as ShopPlan).id == id
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + notice.hashCode()
        result = 31 * result + amount.hashCode()
        result = 31 * result + date.hashCode()
        result = 31 * result + done.hashCode()
        result = 31 * result + createdAt.hashCode()
        result = 31 * result + updatedAt.hashCode()
        result = 31 * result + food.hashCode()
        return result
    }
}