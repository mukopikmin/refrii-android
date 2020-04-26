package app.muko.mypantry.data.models

import java.util.*

open class User {

    open var id: Int = 0
    open var name: String? = null
    open var email: String? = null
    open var provider: String? = null
    open var avatarUrl: String? = null
    open var updatedAt: Date? = null
    open var createdAt: Date? = null
}
