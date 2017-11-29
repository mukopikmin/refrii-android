package com.refrii.client.tasks

import android.accounts.Account
import android.content.Context
import android.os.AsyncTask
import android.util.Log
import com.google.android.gms.auth.GoogleAuthException
import com.google.android.gms.auth.GoogleAuthUtil
import com.google.android.gms.auth.UserRecoverableAuthException
import java.io.IOException
import java.lang.ref.WeakReference

class RetrieveGoogleTokenTask(
        private val context: WeakReference<Context>,
        private val callback: RetrieveGoogleTokenTaskCallback) : AsyncTask<String, Void, String>() {

    override fun doInBackground(vararg accounts: String): String? {
        var token: String? = null

        try {
            val account = Account(accounts[0], GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE)
            token = GoogleAuthUtil.getToken(context.get(), account, AUTH_SCOPE)
        } catch (e: IOException) {
            Log.e(TAG, e.message)
        } catch (e: UserRecoverableAuthException) {
//            startActivityForResult(e.intent, GOOGLE_SIGNIN_REQUEST_CODE)
        } catch (e: GoogleAuthException) {
            Log.e(TAG, e.message)
        }

        return token
    }

    override fun onPostExecute(googleToken: String?) {
        super.onPostExecute(googleToken)

        callback.onPostExecuted(googleToken)
    }

    companion object {
        private const val TAG = "RetrieveGoogleTokenTask"
        private const val AUTH_SCOPE = "oauth2:profile email "
    }
}