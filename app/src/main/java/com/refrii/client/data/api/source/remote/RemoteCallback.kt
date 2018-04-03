package com.refrii.client.data.api.source.remote

interface RemoteCallback<in T> {
    fun onNext(t: T?)
    fun onCompleted()
    fun onError(e: Throwable?)
}