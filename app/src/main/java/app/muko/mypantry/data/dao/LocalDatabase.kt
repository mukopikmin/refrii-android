package app.muko.mypantry.data.dao

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import app.muko.mypantry.data.models.*
import app.muko.mypantry.data.models.Unit

@Database(
        entities = [
            Box::class,
            Food::class,
            Invitation::class,
            Notice::class,
            ShopPlan::class,
            Unit::class,
            User::class
        ],
        version = 1
)
@TypeConverters(Converters::class)
abstract class LocalDatabase : RoomDatabase() {
    abstract fun boxDao(): BoxDao
    abstract fun foodDao(): FoodDao
    abstract fun unitDao(): UnitDao
    abstract fun shopPlanDao(): ShopPlanDao
}