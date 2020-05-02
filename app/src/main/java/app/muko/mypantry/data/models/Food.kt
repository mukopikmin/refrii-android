package app.muko.mypantry.data.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Food(
        @PrimaryKey
        val id: Int,
        val name: String,
        @Embedded(prefix = "notices_")
        val notices: List<Notice>,
        val amount: Double,
        val expirationDate: Date,
        val isNeedsAdding: Boolean,
        val imageUrl: String,
        val createdAt: Date,
        val updatedAt: Date,
        @Embedded(prefix = "unit_")
        val unit: Unit,
        @Embedded(prefix = "created_user_")
        val createdUser: User,
        @Embedded(prefix = "updated_user_")
        val updatedUser: User,
        @Embedded(prefix = "box_")
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
        result = 31 * result + isNeedsAdding.hashCode()
        result = 31 * result + imageUrl.hashCode()
        result = 31 * result + createdAt.hashCode()
        result = 31 * result + updatedAt.hashCode()
        result = 31 * result + unit.hashCode()
        result = 31 * result + createdUser.hashCode()
        result = 31 * result + updatedUser.hashCode()
        result = 31 * result + box.hashCode()
        return result
    }
}

