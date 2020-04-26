package app.muko.mypantry.data.source

import android.content.Context
import android.graphics.Bitmap
import app.muko.mypantry.data.models.Box
import app.muko.mypantry.data.models.Food
import app.muko.mypantry.data.models.ShopPlan
import app.muko.mypantry.data.models.Unit
import app.muko.mypantry.data.source.remote.ApiRemoteFoodSource
import retrofit2.Retrofit
import rx.Observable
import java.util.*

class ApiFoodRepository(context: Context, retrofit: Retrofit) {

    private val mApiRemoteFoodSource = ApiRemoteFoodSource(context, retrofit)

    fun getFoods(): Observable<List<Food>> {
        return mApiRemoteFoodSource.getFoods()
    }


//    fun getFoodsFromCache(): Observable<List<Food>> {
//        return mApiLocalFoodSource.getFoods()
//    }

    fun getFood(id: Int): Observable<Food> {
        return mApiRemoteFoodSource.getFood(id)
    }
//
//    fun getFoodFromCache(id: Int): Observable<Food?> {
//        return mApiLocalFoodSource.getFood(id)
//    }

    fun getShopPlansForFood(id: Int): Observable<List<ShopPlan>> {
        return mApiRemoteFoodSource.getShopPlansForFood(id)
    }

//    fun getShopPlansForFoodFromCache(id: Int): Observable<List<ShopPlan>> {
//        return mApiLocalFoodSource.getShopPlansForFood(id)
//    }

//    fun getExpiringFoods(): Observable<List<Food>> {
//        return mApiLocalFoodSource.getExpiringFoods()
//    }

//    fun getExpiringFoodsFromCache(): Observable<List<Food>> {
//        return mApiLocalFoodSource.getExpiringFoods()
//    }

    fun createFood(name: String, amount: Double, box: Box, unit: Unit, expirationDate: Date): Observable<Food> {
        return mApiRemoteFoodSource.createFood(name, amount, box, unit, expirationDate)
    }

    fun updateFood(id: Int, name: String?, amount: Double?, expirationDate: Date?, bitmap: Bitmap?, boxId: Int?, unitId: Int?): Observable<Food> {
        return mApiRemoteFoodSource.update(id, name, amount, expirationDate, bitmap, boxId, unitId)
    }

    fun removeFood(id: Int): Observable<Void> {
        return mApiRemoteFoodSource.removeFood(id)
    }

    fun createNotice(foodId: Int, text: String): Observable<Food> {
        return mApiRemoteFoodSource.createNotice(foodId, text)
    }
}