package app.muko.mypantry.data.models

import java.util.*

open class Notice  {

    open var id: Int = 0
    open var text: String? = null
    open var createdUser: User? = null
    open var updatedUser: User? = null
    open var createdAt: Date? = null
    open var updatedAt: Date? = null
}