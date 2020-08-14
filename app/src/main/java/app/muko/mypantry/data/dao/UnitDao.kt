package app.muko.mypantry.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import app.muko.mypantry.data.models.Unit

@Dao
interface UnitDao {

    @Query("SELECT * FROM unit")
    fun getAllLiveData(): LiveData<List<Unit>>

    @Query("SELECT * FROM unit")
    fun getAll(): List<Unit>

    @Query("SELECT * FROM unit WHERE id = :id")
    fun getLiveData(id: Int): LiveData<Unit>

    @Query("SELECT * FROM unit WHERE id = :id")
    fun get(id: Int): Unit

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdate(unit: Unit)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdate(unit: List<Unit>)

    @Update
    fun update(unit: Unit)

    @Delete
    fun delete(unit: Unit)
}