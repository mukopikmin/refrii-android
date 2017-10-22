package com.refrii.client

import android.content.Context
import android.content.Intent
import android.util.Log

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by yusuke on 2017/09/28.
 */

open class BasicCallback<T>(private val mContext: Context) : Callback<T> {

    override fun onResponse(call: Call<T>, response: Response<T>) {
        val intent: Intent

        Log.e(TAG, "" + response.code())

        when (response.code()) {
            401, 403, 404 -> {
                intent = Intent(mContext, SigninActivity::class.java)
                mContext.startActivity(intent)
            }
            500 -> {
                intent = Intent(mContext, ErrorActivity::class.java)
                mContext.startActivity(intent)
            }
        }
    }

    override fun onFailure(call: Call<T>, t: Throwable) {
        Log.e(TAG, t.message)
    }

    companion object {

        private val TAG = "BasicCallback"
    }
}
