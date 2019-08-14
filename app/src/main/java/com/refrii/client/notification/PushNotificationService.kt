package com.refrii.client.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.RingtoneManager
import android.os.Build
import android.preference.PreferenceManager
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.refrii.client.App
import com.refrii.client.R
import com.refrii.client.data.source.ApiUserRepository
import com.refrii.client.foodlist.FoodListActivity
import javax.inject.Inject


class PushNotificationService : FirebaseMessagingService() {

    private lateinit var mPreference: SharedPreferences

    @Inject
    lateinit var mApiUserRepository: ApiUserRepository

    override fun onCreate() {
        super.onCreate()

        (application as App).getComponent().inject(this)

        mPreference = PreferenceManager.getDefaultSharedPreferences(applicationContext)
    }

    override fun onNewToken(newToken: String?) {
        super.onNewToken(newToken)

        register(newToken)
    }

    override fun onMessageReceived(message: RemoteMessage?) {
        super.onMessageReceived(message)

        val title = message?.notification?.title
        val body = message?.notification?.body

        sendNotification(title, body)
    }

    private fun register(token: String?) {
        token ?: return

        val userId = mPreference.getInt(application.getString(R.string.preference_key_id), 0)

        mApiUserRepository.registerPushToken(userId, token)
                .subscribe({
                    val editor = mPreference.edit()

                    editor.putString(application.getString(R.string.preference_key_push_token), token)
                    editor.apply()
                }, {
                    Toast.makeText(applicationContext, it.message, Toast.LENGTH_LONG).show()
                })
    }

    private fun sendNotification(title: String?, message: String?) {
        val intent = Intent(this, FoodListActivity::class.java)

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
        val notificationBuilder = NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setSubText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setStyle(NotificationCompat.BigTextStyle().bigText(title))
                .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationBuilder.setChannelId(getString(R.string.notification_channel_id_push))
        }

        notificationManager.notify(0, notificationBuilder.build())
    }

    companion object {
        private const val TAG = "PushNotificationService"
    }
}