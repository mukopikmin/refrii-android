package app.muko.mypantry.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import app.muko.mypantry.data.models.Food
import app.muko.mypantry.data.models.Unit
import io.reactivex.Completable

@Dao
interface UnitDao {

    @Query("SELECT * FROM unit WHERE user_id = :userId")
    fun getByUserLiveData(userId: Int): LiveData<List<Unit>>

    @Query("SELECT * FROM unit WHERE user_id = :userId")
    fun getByUser(userId: Int): List<Food>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdate(unit: Unit): Completable

    @Update
    fun update(unit: Unit)

    @Delete
    fun delete(unit: Unit)
}