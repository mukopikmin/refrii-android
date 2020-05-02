package app.muko.mypantry.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import app.muko.mypantry.data.models.Box
import io.reactivex.Completable

@Dao
interface BoxDao {

    @Query("SELECT * FROM box")
    fun getAll(): LiveData<List<Box>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdate(box: List<Box>): Completable
}