package app.muko.mypantry.data.dao

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import app.muko.mypantry.data.models.Box

@Database(entities = [Box::class], version = 1)
@TypeConverters(Converters::class)
abstract class LocalDatabase : RoomDatabase() {
    abstract fun boxDao(): BoxDao
}