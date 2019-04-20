package com.refrii.client.data.models

import io.realm.RealmList
import io.realm.RealmObject

open class Version : RealmObject() {

    open var name: RealmList<String>? = null
    open var notice: RealmList<String>? = null
}