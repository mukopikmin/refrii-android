package app.muko.mypantry.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class User(
        @PrimaryKey
        val id: Int,
        val name: String,
        val email: String,
        val provider: String,
        val avatarUrl: String,
        val updatedAt: Date,
        val createdAt: Date
) {

    override fun equals(other: Any?): Boolean {
        return (other as User).id == id
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + name.hashCode()
        result = 31 * result + email.hashCode()
        result = 31 * result + provider.hashCode()
        result = 31 * result + avatarUrl.hashCode()
        result = 31 * result + updatedAt.hashCode()
        result = 31 * result + createdAt.hashCode()
        return result
    }
}