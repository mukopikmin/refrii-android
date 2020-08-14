package app.muko.mypantry.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import app.muko.mypantry.data.models.Box
import app.muko.mypantry.data.models.Food

@Dao
interface BoxDao {

    @Query("SELECT * FROM box")
    fun getAllLiveData(): LiveData<List<Box>>

    @Query("SELECT * FROM box")
    fun getAll(): List<Box>

    @Query("SELECT * FROM box WHERE id = :id")
    fun getLiveData(id: Int): LiveData<Box>

    @Query("SELECT * FROM box WHERE id = :id")
    fun get(id: Int): Box

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdate(box: Box)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdate(boxes: List<Box>)

    @Update
    fun update(food: Food)

    @Delete
    fun delete(box: Box)
}