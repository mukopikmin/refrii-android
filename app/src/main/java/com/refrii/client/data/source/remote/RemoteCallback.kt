package com.refrii.client.data.source.remote

interface RemoteCallback<in T> {
    fun onNext(t: T?)
    fun onCompleted()
    fun onError(e: Throwable?)
}