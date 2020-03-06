package app.muko.mypantry.data.source.local

import app.muko.mypantry.data.models.Box
import app.muko.mypantry.data.models.Food
import app.muko.mypantry.data.models.Unit
import app.muko.mypantry.data.models.User
import io.realm.Realm
import io.realm.kotlin.where
import rx.Observable

class ApiLocalBoxSource(private val mRealm: Realm) {

    fun getBoxes(): Observable<List<Box>> {
        val boxes = mRealm.where(Box::class.java)
                .findAll()
                .sort("id")

        return Observable.just(mRealm.copyFromRealm(boxes))
    }

    fun saveBox(box: Box): Observable<Box?> {
        mRealm.executeTransaction {
            it.copyToRealmOrUpdate(box)
        }

        return getBox(box.id)
    }

    fun getUnitsForBox(id: Int): Observable<List<Unit>> {
        val box = mRealm.where<Box>()
                .equalTo("id", id)
                .findFirst()
        val user = mRealm.where<User>()
                .equalTo("id", box?.owner?.id)
                .findFirst()
        val units = mRealm.where<Unit>()
                .equalTo("user.id", user?.id)
                .findAll()
                .sort("id")

        return Observable.just(mRealm.copyFromRealm(units))
    }

    fun getBox(id: Int): Observable<Box?> {
        val box = mRealm.where<Box>()
                .equalTo("id", id)
                .findFirst()

        return Observable.just(mRealm.copyFromRealm(box))
    }

    fun updateBox(box: Box): Observable<Box?> {
        mRealm.executeTransaction {
            it.copyToRealmOrUpdate(box)
        }

        return getBox(box.id)
    }

    fun removeBox(id: Int): Observable<Void> {
        mRealm.executeTransaction { realm ->
            realm.where<Box>()
                    .equalTo("id", id)
                    .findAll()
                    .deleteAllFromRealm()
        }

        return Observable.empty()
    }

    fun getFoodsInBox(id: Int): Observable<List<Food>> {
        val foods = mRealm.where<Food>()
                .equalTo("box.id", id)
                .findAll()
                .sort("id")

        return Observable.just(mRealm.copyFromRealm(foods))
    }
}