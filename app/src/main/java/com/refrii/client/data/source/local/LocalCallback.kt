package com.refrii.client.data.source.local

interface LocalCallback {
    fun <T> onSuccess(t: T)
    fun onError(e: Throwable)
}