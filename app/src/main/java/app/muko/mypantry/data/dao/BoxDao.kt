package app.muko.mypantry.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import app.muko.mypantry.data.models.Box
import app.muko.mypantry.data.models.Food
import io.reactivex.Completable

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
    fun insertOrUpdate(box: Box): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdate(boxes: List<Box>): Completable

    @Update
    fun update(food: Food): Completable

    @Delete
    fun delete(box: Box): Completable
}