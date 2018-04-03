package com.refrii.client.data.api.source

interface ApiRepositoryCallback<in T> {
    fun onNext(t: T?)
    fun onCompleted()
    fun onError(e: Throwable?)
}