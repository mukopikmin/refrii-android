package com.refrii.client.tasks

interface RetrieveGoogleTokenTaskCallback {
    fun onPostExecuted(result: String?)
}