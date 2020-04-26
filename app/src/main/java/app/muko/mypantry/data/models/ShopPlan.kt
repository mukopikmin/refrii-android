package app.muko.mypantry.data.models

import java.io.Serializable
import java.util.*

open class ShopPlan {

    open var id: Int = 0
    open var notice: String? = null
    open var amount: Double = 0.toDouble()
    open var date: Date? = null
    open var done: Boolean = false
    open var createdAt: Date? = null
    open var updatedAt: Date? = null
    open var food: Food? = null
}
