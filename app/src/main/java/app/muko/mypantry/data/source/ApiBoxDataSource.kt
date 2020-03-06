package app.muko.mypantry.data.source

import app.muko.mypantry.data.models.Box
import app.muko.mypantry.data.models.Food
import app.muko.mypantry.data.models.Invitation
import app.muko.mypantry.data.models.Unit
import rx.Observable

interface ApiBoxDataSource {

    fun getBoxes(): Observable<List<Box>>
    fun getBoxesFromCache(): Observable<List<Box>>
    fun getBox(id: Int): Observable<Box?>
    fun getFoodsInBox(id: Int): Observable<List<Food>>
    fun getFoodsInBoxFromCache(id: Int): Observable<List<Food>>
    fun getBoxFromCache(id: Int): Observable<Box?>
    fun createBox(name: String, notice: String?): Observable<Box>
    fun getUnitsForBox(id: Int): Observable<List<Unit>>
    fun getUnitsForBoxFromCache(id: Int): Observable<List<Unit>>
    fun updateBox(id: Int, name: String?, notice: String?): Observable<Box>
    fun removeBox(id: Int): Observable<Void>
    fun invite(boxId: Int, email: String): Observable<Invitation>
    fun uninvite(boxId: Int, email: String): Observable<Void>
}