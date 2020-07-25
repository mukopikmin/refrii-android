package app.muko.mypantry.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import app.muko.mypantry.data.models.Food
import io.reactivex.Completable

@Dao
interface FoodDao {

    @Query("SELECT * FROM food")
    fun getAllLiveData(): LiveData<List<Food>>

    @Query("SELECT * FROM food")
    fun getAll(): List<Food>

    @Query("SELECT * FROM food WHERE id = :id")
    fun getLiveData(id: Int): LiveData<Food>

    @Query("SELECT * FROM food WHERE id = :id")
    fun get(id: Int): Food

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdate(food: Food): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdate(foods: List<Food>): Completable

    @Update
    fun update(food: Food)

    @Delete
    fun delete(food: Food)
}