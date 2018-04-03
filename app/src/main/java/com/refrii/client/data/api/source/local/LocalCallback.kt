package com.refrii.client.data.api.source.local

interface LocalCallback {
    fun <T> onSuccess(t: T)
    fun onError(e: Throwable)
}