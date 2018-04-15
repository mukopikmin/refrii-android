package com.refrii.client.data.api.models

import io.realm.RealmList
import io.realm.RealmObject

open class BoxChangeSet : RealmObject() {

    open var name: RealmList<String>? = null
    open var notice: RealmList<String>? = null
}