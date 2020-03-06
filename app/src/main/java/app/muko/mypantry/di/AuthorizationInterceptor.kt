package app.muko.mypantry.di

import android.content.Context
import android.net.ConnectivityManager
import android.preference.PreferenceManager
import app.muko.mypantry.R
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import okhttp3.*
import java.util.*

class AuthorizationInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val expiresAt = sharedPreferences.getLong(context.getString(R.string.preference_key_expiration_timestamp), 0) * 1000
        val currentUser = FirebaseAuth.getInstance().currentUser
        val original = chain.request()
        val jwt = sharedPreferences.getString(context.getString(R.string.preference_key_jwt), null)
        val request = original.newBuilder()
                .header("Accept", "application/json")
                .header("Authorization", "Bearer $jwt")
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo

        if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
            if (expiresAt < Date().time) {
                val task = currentUser?.getIdToken(true)?.addOnCompleteListener {
                    val editor = sharedPreferences.edit()

                    editor.apply {
                        putString(context.getString(R.string.preference_key_jwt), it.result?.token)

                        it.result?.expirationTimestamp?.let {
                            putLong(context.getString(R.string.preference_key_expiration_timestamp), it)
                        }
                    }
                    editor.apply()
                }

                if (task != null) {
                    Tasks.await(task).token?.let {
                        request.header("Authorization", "Bearer $it")
                    }
                }
            }

            request.method(original.method(), original.body())

            return chain.proceed(request.build())
        } else {
            return Response.Builder()
                    .code(0)
                    .protocol(Protocol.HTTP_2)
                    .message("No internet connection")
                    .request(original)
                    .body(ResponseBody.create(MediaType.parse("text/plain"), ""))
                    .build()
        }
    }
}