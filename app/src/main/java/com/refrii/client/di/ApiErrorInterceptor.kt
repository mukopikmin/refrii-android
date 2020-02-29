package com.refrii.client.di

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.refrii.client.signin.SignInActivity
import okhttp3.Interceptor
import okhttp3.Response

class ApiErrorInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())

        when (response.code()) {
            401 -> {
                val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
                val tasks = activityManager.appTasks
                val activities = tasks.map { it.taskInfo.topActivity.toString() }

                FirebaseAuth.getInstance().signOut()

                if (!activities.contains(context.packageName + ".signin.SignInActivity")) {
                    val intent = Intent(context, SignInActivity::class.java)

                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                }
            }
        }

        return response
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
}