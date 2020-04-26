package app.muko.mypantry.data.source

import app.muko.mypantry.data.models.Box
import app.muko.mypantry.data.models.Food
import app.muko.mypantry.data.models.Invitation
import app.muko.mypantry.data.models.Unit
import app.muko.mypantry.data.source.remote.ApiRemoteBoxSource
import retrofit2.Retrofit
import rx.Observable

class ApiBoxRepository(retrofit: Retrofit) {

    private val mApiRemoteBoxSource = ApiRemoteBoxSource(retrofit)

    fun getBoxes(): Observable<List<Box>> {
        return mApiRemoteBoxSource.getBoxes()
    }

//    fun getBoxesFromCache(): Observable<List<Box>> {
//        return mApiLocalBoxSource.getBoxes()
//    }

    fun getBox(id: Int): Observable<Box> {
        return mApiRemoteBoxSource.getBox(id)
    }

    fun getFoodsInBox(id: Int): Observable<List<Food>> {
        return mApiRemoteBoxSource.getFoodsInBox(id)
    }
//
//    fun getFoodsInBoxFromCache(id: Int): Observable<List<Food>> {
//        return mApiLocalBoxSource.getFoodsInBox(id)
//    }
//
//    fun getBoxFromCache(id: Int): Observable<Box?> {
//        return mApiLocalBoxSource.getBox(id)
//    }

    fun createBox(name: String, notice: String?): Observable<Box> {
        return mApiRemoteBoxSource.createBox(name, notice)
    }

    fun getUnitsForBox(id: Int): Observable<List<Unit>> {
        return mApiRemoteBoxSource.getUnitsForBox(id)
    }

//    fun getUnitsForBoxFromCache(id: Int): Observable<List<Unit>> {
//        return mApiLocalBoxSource.getUnitsForBox(id)
//    }

    fun updateBox(id: Int, name: String?, notice: String?): Observable<Box> {
        return mApiRemoteBoxSource.updateBox(id, name, notice)
    }

    fun removeBox(id: Int): Observable<Void> {
        return mApiRemoteBoxSource.removeBox(id)
    }

    fun invite(boxId: Int, email: String): Observable<Invitation> {
        return mApiRemoteBoxSource.invite(boxId, email)
    }
}