package app.muko.mypantry.helpers

import org.mockito.Mockito

@Suppress("UNCHECKED_CAST")
class MockitoHelper {
    companion object {
        fun <T> any(): T {
            return Mockito.any()
                    ?: null as T
        }

        fun <T> eq(value: T): T {
            return if (value != null)
                Mockito.eq(value)
            else
                null
                        ?: null as T
        }
    }
}