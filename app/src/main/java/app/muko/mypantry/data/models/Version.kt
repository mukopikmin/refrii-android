package app.muko.mypantry.data.models

import androidx.room.Entity

@Entity
data class Version(
        val name: List<String>,
        val notice: List<String>
)
