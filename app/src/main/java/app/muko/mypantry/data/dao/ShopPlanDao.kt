package app.muko.mypantry.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import app.muko.mypantry.data.models.ShopPlan

@Dao
interface ShopPlanDao {

    @Query("SELECT * FROM shop_plan WHERE done = 0")
    fun getAllLiveData(): LiveData<List<ShopPlan>>

    @Query("SELECT * FROM shop_plan WHERE done = 0")
    fun getAll(): List<ShopPlan>

    @Query("SELECT * FROM shop_plan WHERE food_id = :foodId AND done = 0")
    fun getLiveDataByFood(foodId: Int): LiveData<List<ShopPlan>>

    @Query("SELECT * FROM shop_plan WHERE food_id = :foodId AND done = 0")
    fun getByFood(foodId: Int): List<ShopPlan>

    @Query("SELECT * FROM shop_plan WHERE id = :id")
    fun get(id: Int): ShopPlan?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdate(shopPlan: ShopPlan)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdate(shopPlans: List<ShopPlan>)

    @Update
    fun update(shopPlan: ShopPlan)

    @Delete
    fun delete(shopPlan: ShopPlan)
}