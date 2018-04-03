package com.refrii.client.data.google

interface GoogleDataSource {

    fun getToken(accountName: String, callback: GoogleRepositoryCallback)
}