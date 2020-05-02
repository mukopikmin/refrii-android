package app.muko.mypantry.data.source

import android.content.Context
import android.graphics.Bitmap
import app.muko.mypantry.data.models.Box
import app.muko.mypantry.data.models.Food
import app.muko.mypantry.data.models.ShopPlan
import app.muko.mypantry.data.models.Unit
import app.muko.mypantry.data.source.remote.ApiRemoteFoodSource
import io.reactivex.Flowable
import retrofit2.Retrofit
import java.util.*

class ApiFoodRepository(context: Context, retrofit: Retrofit) {

    private val mApiRemoteFoodSource = ApiRemoteFoodSource(context, retrofit)

    fun getFoods(): Flowable<List<Food>> {
        return mApiRemoteFoodSource.getFoods()
    }


//    fun getFoodsFromCache(): Flowable<List<Food>> {
//        return mApiLocalFoodSource.getFoods()
//    }

    fun getFood(id: Int): Flowable<Food> {
        return mApiRemoteFoodSource.getFood(id)
    }
//
//    fun getFoodFromCache(id: Int): Flowable<Food?> {
//        return mApiLocalFoodSource.getFood(id)
//    }

    fun getShopPlansForFood(id: Int): Flowable<List<ShopPlan>> {
        return mApiRemoteFoodSource.getShopPlansForFood(id)
    }

//    fun getShopPlansForFoodFromCache(id: Int): Flowable<List<ShopPlan>> {
//        return mApiLocalFoodSource.getShopPlansForFood(id)
//    }

//    fun getExpiringFoods(): Flowable<List<Food>> {
//        return mApiLocalFoodSource.getExpiringFoods()
//    }

//    fun getExpiringFoodsFromCache(): Flowable<List<Food>> {
//        return mApiLocalFoodSource.getExpiringFoods()
//    }

    fun createFood(name: String, amount: Double, box: Box, unit: Unit, expirationDate: Date): Flowable<Food> {
        return mApiRemoteFoodSource.createFood(name, amount, box, unit, expirationDate)
    }

    fun updateFood(id: Int, name: String?, amount: Double?, expirationDate: Date?, bitmap: Bitmap?, boxId: Int?, unitId: Int?): Flowable<Food> {
        return mApiRemoteFoodSource.update(id, name, amount, expirationDate, bitmap, boxId, unitId)
    }

    fun removeFood(id: Int): Flowable<Void> {
        return mApiRemoteFoodSource.removeFood(id)
    }

    fun createNotice(foodId: Int, text: String): Flowable<Food> {
        return mApiRemoteFoodSource.createNotice(foodId, text)
    }
}