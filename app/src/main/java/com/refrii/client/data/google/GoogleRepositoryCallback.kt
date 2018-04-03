package com.refrii.client.data.google

interface GoogleRepositoryCallback {

    fun onSuccess(token: String)
    fun onError()
}