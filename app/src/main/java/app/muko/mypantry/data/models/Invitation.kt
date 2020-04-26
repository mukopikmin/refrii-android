package app.muko.mypantry.data.models

import java.util.*

open class Invitation  {

    open var id: Int = 0
    open var user: User? = null
    open var createdAt: Date? = null
    open var updatedAt: Date? = null
}