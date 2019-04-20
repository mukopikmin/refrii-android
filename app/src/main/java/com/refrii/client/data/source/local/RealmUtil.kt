package com.refrii.client.data.source.local

import io.realm.Realm
import io.realm.RealmConfiguration

object RealmUtil {

    fun getInstance(): Realm {
        val config = RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build()

        return Realm.getInstance(config)
    }
}