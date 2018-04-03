package com.refrii.client.data.google

import android.content.Context
import com.refrii.client.tasks.RetrieveGoogleTokenTask
import com.refrii.client.tasks.RetrieveGoogleTokenTaskCallback
import java.lang.ref.WeakReference

class GoogleRepository(private val mContext: Context) : GoogleDataSource {

    override fun getToken(accountName: String, callback: GoogleRepositoryCallback) {
        RetrieveGoogleTokenTask(WeakReference(mContext), object : RetrieveGoogleTokenTaskCallback {
            override fun onPostExecuted(result: String?) {
                if (result == null) {
                    result ?: callback.onError()
                } else {
                    callback.onSuccess(result)
                }
            }
        }).execute(accountName)
    }
}